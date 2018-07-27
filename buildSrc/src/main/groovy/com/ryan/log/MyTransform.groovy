package com.ryan.log

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class MyTransform extends Transform {

    Project mProject
    LogConfig mConfig

    MyTransform(Project project, LogConfig logConfig) {
        mProject = project
        mConfig = logConfig
    }

    @Override
    String getName() {
        return "Ryan"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
//        return TransformManager.PROJECT_ONLY
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("executing transform start")
        //输入
        Collection<TransformInput> inputs = transformInvocation.inputs
        //输出
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (inputs) {
            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput dirInput ->
                    println "---------------------"
                    println "directoryInputs begin"
                    println "---------------------"
                    //输出目录
                    File outputDir = outputProvider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY)
                    //输入目录
                    File inputDir = dirInput.file
                    println "outputDir = $outputDir.absolutePath"
                    println "inputDIr = $inputDir.absolutePath"
                    //复制文件...作为下一个transform的输入
                    FileUtils.copyDirectory(inputDir, outputDir)
                    //遍历当前transform下的文件,做AOP操作
                    outputDir.traverse { original ->
                        if (original.isFile()) {
//                            println ""
//                            println("child file = $original.absolutePath")
//                            println ""

                            boolean needModify = checkNeedModify(original)
                            if (needModify) {
                                println ":::::::::::::::::::::::::::::"
                                println "File: ${original.name} need modify  = ${needModify}"
                                println ":::::::::::::::::::::::::::::"
                            }

                            if (needModify) {
                                modifyClass(original)
                            }
                        }
                    }
                    println "---------------------"
                    println "directoryInputs   end"
                    println "---------------------"

                }

                input.jarInputs.each {
                    File outputJar = outputProvider.getContentLocation(
                            it.name,
                            it.contentTypes,
                            it.scopes,
                            Format.JAR)
//                    println("Jarinput path = ${it.toString()}")
//                    println("Jarinput name = ${it.name}")
//                    println("outputJar name = ${outputJar.name}")

                    File inputJar = it.file
                    FileUtils.copyFile(inputJar, outputJar)

                }
            }
        }


        boolean inCremental = transformInvocation.incremental
        println("inCremental = ${incremental}")

        Collection<TransformInput> referencedInputs = transformInvocation.referencedInputs

        if (referencedInputs) {
            println("referencedInputs != null")
        }

        Collection<SecondaryInput> secondaryInputs = transformInvocation.secondaryInputs

        if (secondaryInputs) {
            println("secondaryInputs != null")
        }
        println "executing transform end"
    }

    void modifyClass(File file) {
        println("=====modifyClass start=====")
        println("=====modifyClass start=====")
        println("=====modifyClass start=====")

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)

        ClassPrinter cp = new ClassPrinter(Opcodes.ASM5, cw)

        ClassReader cr = new ClassReader(file.newInputStream())

        cr.accept(cp, ClassReader.EXPAND_FRAMES)

        byte[] b2 = cw.toByteArray()

        println "output byte array's length = ${b2.length}"

        if (file.exists()) {
            file.delete()
        }

        file.createNewFile()


        BufferedOutputStream bos =  file.newOutputStream()

        bos.write(b2)

        bos.flush()

        bos.close()

        println("=====modifyClass end=====")
        println("=====modifyClass end=====")
        println("=====modifyClass end=====")

    }

    boolean checkNeedModify(File inputFile) {
        boolean isNeed = false
        if (mConfig.includeClass) {
            String[] clazz = mConfig.includeClass
            clazz.each {
//                if (inputDir.absolutePath.contains(
//                        it.replace(".", "\\"))) {
//                    isNeed = true
//                }


                String clazzNeedChanged = it.replace(".", "\\")

                if (inputFile != null &&
                        inputFile.name != null &&
                        inputFile.canonicalPath.contains(clazzNeedChanged )) {

                    println "当前进行转换的类的路径 = " + inputFile.canonicalPath
                    println "当前类的名称 ${inputFile.name}"
                    println "当前类的名称,去除.class = " + inputFile.getName().replace(".class", "")
                    println "需要进行转换的类 = $it"
                    println "需要进行转换的类,内部名称 = $clazzNeedChanged"

                    isNeed = true
                }

            }
        } else if (mConfig.includePkg) {
            String pkg = mConfig.includePkg;
            if (inputFile.absolutePath.contains(
                    pkg.replace(".", "\\"))) {
                isNeed = true
            }
        }

        return isNeed
    }

}
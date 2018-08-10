package com.ryan.log

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class HMTTransform extends Transform {

    Project mProject
    HMTConfig mConfig
    ILog mLog
    String TAG = com.ryan.log.HMTTransform.class.getSimpleName()
    AppExtension mAppExtension

    HMTTransform(Project project, HMTConfig config, ILog log, AppExtension app) {
        mProject = project
        mConfig = config
        mLog = log
        mAppExtension = app
    }

    @Override
    String getName() {
        return "HMT"
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
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        mLog.e(TAG, "executing transform start")

        mLog.e(TAG, "include pkg  = " + mConfig.includePkg)
        //输入
        Collection<TransformInput> inputs = transformInvocation.inputs
        //输出
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (inputs) {
            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput dirInput ->
                    mLog.e(TAG, "directoryInputs begin")
                    //输出目录
                    File outputDir = outputProvider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY)
                    //输入目录
                    File inputDir = dirInput.file
                    //复制文件...作为下一个transform的输入
                    FileUtils.copyDirectory(inputDir, outputDir)

                    //遍历当前transform目录下的文件,对符合条件的做AOP操作
                    outputDir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File classFile ->
                            boolean needModify = checkNeedModify(classFile)
                            if (needModify) {
                                mLog.e(TAG, "File [ ${classFile.name} ] need modify")
                                modifyClass(classFile,
                                        mProject.buildDir)
                            }
                    }


                    mLog.e(TAG, "directoryInputs   end")
                }

                input.jarInputs.each {
                    mLog.e(TAG, "jarInputs begin")
                    File outputJar = outputProvider.getContentLocation(
                            it.name,
                            it.contentTypes,
                            it.scopes,
                            Format.JAR)
                    mLog.e TAG, "jar file  = [$it.file.name]"
                    mLog.e(TAG, "out file  = [$outputJar.name]")
                    mLog.e(TAG, "out file path = [$outputJar.absolutePath]")
                    File inputJar = it.file
                    FileUtils.copyFile(inputJar, outputJar)

                    if (mConfig.enableJar) {

                    }

                    mLog.e(TAG, "jarInputs end")

                }
            }
        }

        mLog.e(TAG, "executing transform end")

    }

    void modifyClass(File targetFile, File tempDir) {

        mLog.e(TAG, "modifyClass start")

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)

        ClassPrinter cp = new ClassPrinter(Opcodes.ASM5, cw, mLog)

        ClassReader cr = new ClassReader(targetFile.newInputStream())

        cr.accept(cp, ClassReader.EXPAND_FRAMES)

        if (cp.isNeedModified) {
            writeByteCode2File(cw, targetFile, tempDir)
        }


        mLog.e(TAG, "modifyClass end")


    }

    private void writeByteCode2File(ClassWriter cw, File targetFile, File tempDir) {
        byte[] b2 = cw.toByteArray()

        mLog.d(TAG, "output byte array's length = [${b2.length}]")

        if (targetFile.exists()) {
            targetFile.delete()
        }

        targetFile.createNewFile()

        BufferedOutputStream bos

        try {
            bos = targetFile.newOutputStream()

            bos.write(b2)

            bos.flush()

        } catch (IOException e) {
            mLog.e(TAG, "output byte error [$e.getMessage()]")
            if (bos != null) {
                bos.close()
            }
        } finally {
            if (bos != null) {
                bos.close()
            }
        }

        if (mConfig.enableOutputModifiedFile) {

            tempDir = new File(tempDir, "HMT-Temp")

            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }


            String targetFileName = targetFile.getName()

            File tempFile = new File(tempDir, targetFileName)

            if (tempFile.exists()) {
                tempFile.delete()
            }

            tempFile.createNewFile()

            try {
                bos = tempFile.newOutputStream()

                bos.write(b2)

                bos.flush()

            } catch (IOException e) {
                if (bos != null) {
                    bos.close()
                }
            } finally {
                if (bos != null) {
                    bos.close()
                }
            }
            mLog.e(TAG, "output modified file to TempDir[$tempDir.absolutePath]")

        }
    }

    /**
     * 根据build.gradle中的配置 决定类是否进行字节码修改
     * @param inputFile
     * @return
     */
    boolean checkNeedModify(File inputFile) {
        boolean isNeed = false
        if (mConfig.includePkg) {
            mConfig.includePkg.each { String pkg ->

                pkg = pkg.replace(".", "\\")

                if (inputFile != null &&
                        !Utils.isEmpty(pkg) &&
                        inputFile.absolutePath.contains(pkg)) {


                    mLog.e(TAG, "当前进行转换的类的路径 = $inputFile.absolutePath")
                    mLog.e(TAG, "当前进行转换的类的名称 ${inputFile.name}")
                    mLog.e(TAG, "当前类的名称,去除.class = " + inputFile.getName().replace(".class", ""))
                    mLog.e(TAG, "需要进行转换的包名 = $pkg")

                    String targetClassName = inputFile.getName().replace(".class", "")
                    mLog.e(TAG, "targetClassName = $targetClassName")

                    //去除R文件 和 BuildConfig文件
                    if (targetClassName.contains("R\$") ||
                            targetClassName.endsWith("R") ||
                            targetClassName.endsWith("BuildConfig")) {
                        return isNeed
                    }

//                    if (!mConfig.excludeClass) {
//                        isNeed = true
//                    }
                    mConfig.excludeClass.each {
                        String excludeClassName ->
                            excludeClassName = excludeClassName.replace(".",
                                    "\\")
                            mLog.e(TAG, "excludeClassName = $excludeClassName")
                            if (inputFile.absolutePath.contains(excludeClassName)) {
                                mLog.e(TAG, "file [$targetClassName] in the excludeClass List")
                                isNeed = false
                            } else {
                                isNeed = true
                            }
                    }
                }
            }
        } else {
            isNeed = true
        }

        return isNeed
    }

    /**
     * 获取应用程序包名
     * @return
     */
    private String getAppPackageName() {
        String packageName
        try {
            def manifestFile = mAppExtension.sourceSets.main.manifest.srcFile
            mLog.d(TAG, "XmlParser manifestFile: " + manifestFile)
            packageName = new XmlParser().parse(manifestFile).attribute('package')
            mLog.d(TAG, "XmlParser packageName: " + packageName)
        } catch (Exception e) {
            mLog.d(TAG, "XmlParser Exception: " + e.getMessage())
        }
        return packageName
    }

}
package com.ryan.log

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

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

        println("transform was executed")
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (inputs) {
            inputs.each {
                it.directoryInputs.each {
                    println "-----directoryInputs begin-----"
                    File outputDir = outputProvider.getContentLocation(
                            it.name,
                            it.contentTypes,
                            it.scopes,
                            Format.DIRECTORY)
                    File inputDir = it.file
                    FileUtils.copyDirectory(inputDir, outputDir)


                    outputDir.traverse { original ->
                        if (original.isFile()) {
                            println("child file = $original.absolutePath")
                            boolean result = checkNeedModify(original)
                            println "file: $original.name need modify  = $result"
                            if (result) {
                                File modifiedFile = modifyClass(original)
                                modifiedFile.withInputStream { input ->
                                    original.withOutputStream { output ->
                                        output << input
                                    }
                                }

                            }
                        }
                    }

                    println "-----directoryInputs end-----"
                    println(" ")

                }

                it.jarInputs.each {
//                    println "-----jarInputs begin-----"
                    File outputJar = outputProvider.getContentLocation(
                            it.name,
                            it.contentTypes,
                            it.scopes,
                            Format.JAR)
//                    println "JAR = " + outputJar.absolutePath
//                    println "JAR = " + it.name
//                    println "JAR = " + it.contentTypes.toString()
//                    println "JAR = " + it.scopes.toString()
                    File inputJar = it.file
//                    println("JarInput = " + inputJar.absolutePath)
                    FileUtils.copyFile(inputJar, outputJar)
//                    println(" ")

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
    }

    File modifyClass(File file) {
        println("=====modifyClass=====")
        return file

    }

    boolean checkNeedModify(File inputDir) {
        boolean isNeed = false
        if (mConfig.includeClass) {
            String[] clazz = mConfig.includeClass
            clazz.each {
                if (inputDir.absolutePath.contains(it.replace(".", "\\"))) {
                    isNeed = true
                }
            }
        } else if (mConfig.includePkg) {
            String pkg = mConfig.includePkg;
            if (inputDir.absolutePath.contains(pkg.replace(".", "\\"))) {
                isNeed = true
            }
        }
        return isNeed
    }

}
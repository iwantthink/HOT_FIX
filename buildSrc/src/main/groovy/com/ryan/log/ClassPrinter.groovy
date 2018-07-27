package com.ryan.log

import org.objectweb.asm.*

class ClassPrinter extends ClassVisitor {

    boolean hasOnResume = false
    boolean hasOnPause = false
    def className

    ClassPrinter(int api) {
        super(api)
    }


    ClassPrinter(int api, ClassVisitor cv) {
        super(api, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        println "---------visit---------"
        println "version = $version"
        println "access = $access"
        println "name = $name"
        println "signature = $signature"
        println "superName = $superName"
        println "interfaces = $interfaces"

        className = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    void visitSource(String source, String debug) {
        super.visitSource(source, debug)
//        println  "visitSource"

    }

    @Override
    void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc)
        println "---------visitOuterClass---------"
        println "owner = $owner"
        println "name = $name"
        println "desc = $desc"

    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible)
    }

    @Override
    AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible)
    }

    @Override
    void visitAttribute(Attribute attr) {
        super.visitAttribute(attr)
//        println  "visitAttribute"
    }

    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        println "---------visitInnerClass---------"
        println "name = $name"
        println "outerName = $outerName"
        println "innerName = $innerName"
        println "access = $access"

        super.visitInnerClass(name, outerName, innerName, access)


    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        println "---------visitField---------"
        println "access = $access"
        println "name = $name"
        println "desc = $desc"
        println "signature = $signature"
        println "value = $value"
        return super.visitField(access, name, desc, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        println "---------visitMethod---------"
        println "access = $access"
        println "name = $name"
        println "desc = $desc"
        println "signature = $signature"
        println "exceptions = $exceptions"

        if (name.equals("onResume")) {
            hasOnResume = true
        }

        if (name.equals("onPause")) {
            hasOnPause = true
        }

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
        if (name.equals("onCreate") && desc.equals("(Landroid/os/Bundle;)V")) {
            mv = new ChangeMethodAdapter(Opcodes.ASM5, mv)
        }
        return mv
    }

    @Override
    void visitEnd() {

        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                "onResume",
                "()V",
                null,
                null)
        Opcodes.ACC_PUBLIC
        mv.visitCode()
        //往帧栈插入 this
        mv.visitVarInsn(Opcodes.ALOAD,
                0)
        //执行
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "android/app/Activity",
                "onResume",
                "()V",
                false)

        //插入字符串
        mv.visitLdcInsn(className)
        mv.visitLdcInsn("OnResume Executed!!!!")
        //执行方法
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "android/util/Log",
                "e",
                "(Ljava/lang/String;Ljava/lang/String;)I",
                false)
        //弹出
        mv.visitInsn(Opcodes.POP)

//        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
//        mv.visitLdcInsn("OnResume!!!!!!!!!!!")
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()

        super.visitEnd()
//        println "visitEnd"
    }
}
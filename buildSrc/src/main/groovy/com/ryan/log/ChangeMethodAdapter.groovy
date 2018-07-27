package com.ryan.log

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ChangeMethodAdapter extends MethodVisitor {


    ChangeMethodAdapter(int api) {
        super(api)
    }

    ChangeMethodAdapter(int api, MethodVisitor mv) {
        super(api, mv)
    }

    @Override
    void visitCode() {
        super.visitCode()
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("hello ASM!")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    }
}
package com.ryan.log

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter


class RedefineAd extends AdviceAdapter {

    protected RedefineAd(MethodVisitor mv, int access, String name,
                         String desc) {
        super(Opcodes.ASM5, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        Label l1 = new Label()
        mv.visitLabel(l1)
        // mv.visitLineNumber(7, l1);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                "Ljava/io/PrintStream;");
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                "currentTimeMillis", "()J", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(J)V", false);
        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                "Ljava/io/PrintStream;");
        mv.visitLdcInsn("After method");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false);
    }

}
package com.ryan.log

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ChangeMethodAdapter extends MethodVisitor {


    ChangeMethodAdapter(int api) {
        super(api)
    }

    private String currentModifyMethod

    ChangeMethodAdapter(int api, MethodVisitor mv, String methodName) {
        super(api, mv)
        currentModifyMethod = methodName
    }

    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc)

        println "---------visitFieldInsn---------"

        println "opcode = $opcode"
        println "owner = $owner"
        println "name = $name"
        println "desc = $desc"


    }

    @Override
    void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index)
        println "---------visitLocalVariable---------"

        println "name = $name"
        println "desc = $desc"
        println "signature = $signature"
        println "start = $start"
        println "end = $end"
        println "index = $index"

    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, owner, name, desc, itf)
        println "---------visitMethodInsn---------"

        println "opcode = $opcode"
        println "owner = $owner"
        println "name = $name"
        println "desc = $desc"
        println "itf = $itf"
    }

    @Override
    void visitInsn(int opcode) {
        super.visitInsn(opcode)

        println "---------visitInsn---------"
        println "opcode = $opcode"
    }

    @Override
    void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand)
        println "---------visitIntInsn---------"
        println "opcode = $opcode"
        println "operand = $operand"

    }

    @Override
    void visitCode() {
        super.visitCode()
        println "---------visitcode---------"

        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        mv.visitLdcInsn("hello ASM!")
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
        println "---------visitEnd---------"

    }
}
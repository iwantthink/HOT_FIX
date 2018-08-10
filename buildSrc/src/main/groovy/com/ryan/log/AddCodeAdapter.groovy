package com.ryan.log

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class AddCodeAdapter extends AdviceAdapter {

    ILog mLog
    String TAG = com.ryan.log.AddCodeAdapter.class.getSimpleName()
    String mClassName = ""
    AOPMethodInfo mAOPMethodInfo

    protected AddCodeAdapter(MethodVisitor mv, AOPMethodInfo aopMethodInfo,
                              int access, String name, String desc,
                              ILog log,
                              String className) {
        super(Opcodes.ASM5, mv, access, name, desc)
        mLog = log
        mClassName = className
        mAOPMethodInfo = aopMethodInfo
    }

    @Override
    void visitInsn(int opcode) {
        super.visitInsn(opcode)

        mLog.d(TAG, "ClassName($mClassName)---- visitInsn\n" +
                "opcode = $opcode")
    }

    @Override
    void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var)
        mLog.d(TAG, "ClassName($mClassName)---- visitVarInsn\n" +
                "opcode = $opcode" +
                "   var = $var")
    }

    @Override
    void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand)
        mLog.d(TAG, "ClassName($mClassName)---- visitVarInsn\n" +
                "opcode = $opcode" +
                "   operand = $operand")
    }

    @Override
    void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst)

        mLog.d(TAG, "ClassName($mClassName)---- visitVarInsn\n" +
                "cst = $cst")
    }

    /**
     * 确定操作的是字段的哪一个方法
     * @param opcode
     * @param owner
     * @param name
     * @param desc
     * @param itf
     */
    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, owner, name, desc, itf)

        mLog.d(TAG, "ClassName($mClassName)---- visitMethodInsn\n" +
                "opcode = $opcode" +
                "   owner = $owner" +
                "   name = $name" +
                "   desc = $desc" +
                "   itf = $itf")
    }

    /**
     * 确定操作的是哪一个字段
     * @param opcode
     * @param owner
     * @param name
     * @param desc
     */
    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc)
        mLog.d(TAG, "ClassName($mClassName)---- visitFieldInsn\n" +
                "opcode = $opcode" +
                "   owner = $owner" +
                "   name = $name" +
                "   desc = $desc")
    }

    @Override
    protected void onMethodEnter() {
        mLog.d(TAG, "ClassName($mClassName)---- onMethodEnter")
        super.onMethodEnter()

    }

    @Override
    protected void onMethodExit(int opcode) {
        mLog.d(TAG, "ClassName($mClassName)---- onMethodExit")
        super.onMethodExit(opcode)

        for (int j = mAOPMethodInfo.paramsStart; j < mAOPMethodInfo.paramsStart + mAOPMethodInfo.paramsCount; j++) {
            mv.visitVarInsn(mAOPMethodInfo.opcodes[j - mAOPMethodInfo.paramsStart], j)
        }
        mv.visitLdcInsn(mClassName)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                mAOPMethodInfo.aopMethodOwner,
                mAOPMethodInfo.aopMethodName,
                mAOPMethodInfo.aopMethodDesc,
                false)

//        mv.visitVarInsn(Opcodes.ALOAD, 1)
//        mv.visitLdcInsn(mClassName)
//        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/ryan/hotfix/HMTTest",
//                "action",
//                "(Landroid/view/View;Ljava/lang/String;Ljava/lang/String;)V",
//                false)
//        mv.visitLdcInsn(mClassName)
//        mv.visitLdcInsn("add aop code")
//        //调用静态方法
//        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
//                "android/util/Log",
//                "e",
//                "(Ljava/lang/String;Ljava/lang/String;)I",
//                false)
//        mv.visitInsn(Opcodes.POP)

    }

    @Override
    void visitCode() {
        super.visitCode()
    }
}
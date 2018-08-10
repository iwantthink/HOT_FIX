package com.ryan.log

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ClassPrinter extends ClassVisitor {

    boolean isNeedModified = false
    boolean hasOnResume = false
    boolean hasOnPause = false
    boolean isActivity = false
    boolean isClickListener = false
    String mCurrentClassName
    String[] mCurrentClassInterface
    ILog mLog
    String TAG = com.ryan.log.ClassPrinter.class.getSimpleName()

    ClassPrinter(int api, ClassVisitor cv, ILog log) {
        super(api, cv)
        mLog = log
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        mLog.d(TAG, "ClassName($mCurrentClassName)---- visit\n" +
                "   version = $version" +
                "   access = $access" +
                "   name = $name" +
                "   signature = $signature" +
                "   interfaces = $interfaces"
        )

        checkIsClickListener(interfaces)
        checkIsActivity(superName, name)
        mCurrentClassName = name
        mCurrentClassInterface = interfaces
        super.visit(version, access, name, signature, superName, interfaces)
    }

    /**
     * 判断该类是否实现onClickListener
     *
     * @param interfaces
     * @return
     */
    private String[] checkIsClickListener(String[] interfaces) {
        interfaces.each {
            if (it.equals('android/view/View$OnClickListener')) {
                isClickListener = true
            }
        }
    }

    private void checkIsActivity(String superName, String name) {
        if (superName.equals("android/support/v7/app/AppCompatActivity")) {
            isActivity = true
        } else {
            isActivity = false
        }
    }

    @Override
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        mLog.d(TAG, "ClassName($mCurrentClassName)---- visitField\n" +
                "   access = $access" +
                "   name = $name" +
                "   desc = $desc" +
                "   signature = $signature" +
                "   value = $value")

        return super.visitField(access, name, desc, signature, value)
    }

    /**
     * 遍历每一个method ,寻找符合要求的,进行AOP操作
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        mLog.d(TAG, "ClassName($mCurrentClassName)---- visitMethod\n" +
                "   access = $access" +
                "   name = $name" +
                "   desc = $desc" +
                "   signature = $signature" +
                "   exceptions = $exceptions")

        checkHasLife(name)

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)

        if (mCurrentClassInterface != null && mCurrentClassInterface.length > 0) {
            AOPMethodInfo methodInfo = AOPConfig.mTargetMethodList.get(name + desc)
            if (methodInfo != null &&
                    mCurrentClassInterface.contains(methodInfo.methodParent)) {
                mLog.e(TAG, "class:[$mCurrentClassName],method:[$name $desc] need modify!!")
                isNeedModified = true
                return new AddCodeAdapter(mv, methodInfo,
                        access, name, desc, mLog, mCurrentClassName)
            }
        }

//        if (isClickListener && name.equals("onClick") && desc.equals("(Landroid/view/View;)V")) {
//            mLog.e(TAG, "isCLickListener")
//            return new AddClickAdapter(Opcodes.ASM5, mv, access,
//                    name, desc, mLog, mCurrentClassName,
//                  'android/view/View$OnClickListener')
//        }
        return mv
    }

    private void checkHasLife(String name) {
        if (name.equals("onResume")) {
            hasOnResume = true
        }

        if (name.equals("onPause")) {
            hasOnPause = true
        }
    }

    @Override
    void visitEnd() {
        addLifeMonitor()
        super.visitEnd()
    }

    private void addLifeMonitor() {
        if (!hasOnResume && isActivity) {
            //创建method适配器
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                    "onResume",
                    "()V",
                    null,
                    null)
            mv.visitCode()
            //执行aload_0,即将this 压栈
            mv.visitVarInsn(Opcodes.ALOAD,
                    0)
            //调用父类的onResume方法
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    "android/app/Activity",
                    "onResume",
                    "()V",
                    false)
            //加载常量
            mv.visitLdcInsn(mCurrentClassName)
            mv.visitLdcInsn("OnResume Executed!!!!")
            //调用静态方法
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "android/util/Log",
                    "e",
                    "(Ljava/lang/String;Ljava/lang/String;)I",
                    false)
            //弹出栈中多余的帧
            mv.visitInsn(Opcodes.POP)

            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(1, 1)
            mv.visitEnd()
        }
    }
}
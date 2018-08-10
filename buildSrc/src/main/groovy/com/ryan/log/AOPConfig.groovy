package com.ryan.log

import org.objectweb.asm.Opcodes

class AOPConfig {

    /**
     * key = [name+desc]
     * value = [AOPMethodInfo]
     */
    public static HashMap<String, AOPMethodInfo> mTargetMethodList = new HashMap<>()

    static {
        initMethodList()
    }

    //    public void onClick(android.view.View);
//    descriptor: (Landroid/view/View;)V
//    flags: ACC_PUBLIC
//    Code:
//    stack=3, locals=2, args_size=2
//    0: aload_1
//    1: ldc           #230                // String com/ryan/hotfix/SampleActivity
//    3: ldc           #232                // String android/view/View$OnClickListener(onClick())
//    5: invokestatic  #238                // Method com/ryan/hotfix/HMTTest.action:(Landroid/view/View;Ljava/lang/String;Ljava/lang/String;)V
//    8: return
//    LocalVariableTable:
//    Start  Length  Slot  Name   Signature
//    0       9     0  this   Lcom/ryan/hotfix/SampleActivity;
//    0       9     1     v   Landroid/view/View;
//    LineNumberTable:
//    line 150: 0

    static void initMethodList() {

        mTargetMethodList.put("onClick(Landroid/view/View;)V",
                new AOPMethodInfo(
                        "onClick",
                        "(Landroid/view/View;)V",
                        'android/view/View$OnClickListener',
                        "action",
                        "(Landroid/view/View;Ljava/lang/String;)V",
                        "com/ryan/hotfix/HMTTest",
                        1,
                        1,
                        [Opcodes.ALOAD]))

        mTargetMethodList.put("onLongClick(Landroid/view/View;)Z",
                new AOPMethodInfo(
                        "onLongClick",
                        "(Landroid/view/View;)Z",
                        'android/view/View$OnLongClickListener',
                        "onLongClick",
                        "(Landroid/view/View;Ljava/lang/String;)V",
                        "com/ryan/hotfix/HMTTest",
                        1,
                        1,
                        [Opcodes.ALOAD]))

    }
}
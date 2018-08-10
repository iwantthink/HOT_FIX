package com.ryan.log

class AOPMethodInfo {

    /**
     * 待修改方法的名称
     */
    String methodName
    /**
     * 待修改方法的签名
     */
    String methodDesc
    /**
     * 待修改方法所在的接口或类
     */
    String methodParent

    /**
     * 埋点方法的名称
     */
    String aopMethodName
    /**
     * 埋点方法的签名
     */
    String aopMethodDesc

    /**
     * 埋点方法所属的类
     */
    String aopMethodOwner

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

    /**
     *
     * 需要采集的 局部变量表中字段的 索引（ 0：this，1+：普通参数 ）
     *
     */
    int paramsStart

    /**
     * 需要采集的 局部变量表中字段的 数量
     */
    int paramsCount

    /**
     * 加载局部变量表中 字段 所需的指令,加载不同类型的参数需要不同的指令
     */
    List<Integer> opcodes

    AOPMethodInfo(String methodName, String methodDesc, String methodParent,
                  String aopMethodName, String aopMethodDesc, String aopMethodOwner,
                  int paramsStart, int paramsCount, List<Integer> opcodes) {
        this.methodName = methodName
        this.methodDesc = methodDesc
        this.methodParent = methodParent
        this.aopMethodName = aopMethodName
        this.aopMethodDesc = aopMethodDesc
        this.aopMethodOwner = aopMethodOwner
        this.paramsStart = paramsStart
        this.paramsCount = paramsCount
        this.opcodes = opcodes
    }
}
package com.ryan.hotfix;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by renbo on 2018/5/24.
 */

public class OriginalClipBinderProxy implements InvocationHandler {

    /**
     * 转换之后的对象
     */
    private Object mBase;
    private Object mOriginalBase;

    /**
     * @param base 转换之前的对象
     */
    public OriginalClipBinderProxy(IBinder base) {
        mOriginalBase = base;
        try {
            //实际上调用的得是Stub.Proxy类型,所以得通过asInterface进行类型转换
            //获取转换之后的IBinder
            Class clipboardStub_Class = Class.forName("android.content.IClipboard$Stub");
            Method asInterface_Method = clipboardStub_Class.getDeclaredMethod("asInterface", IBinder.class);
            asInterface_Method.setAccessible(true);
            mBase = asInterface_Method.invoke(null, base);


            //hook Stub.Proxy的方法
            //注意这里有三个接口
            mBase = Proxy.newProxyInstance(mBase.getClass().getClassLoader(),
                    new Class[]{IBinder.class,
                            IInterface.class,
                            Class.forName("android.content.IClipboard")},
                    new ProxyClipBinderProxy(mBase));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Log.d("OriginalClipBinderProxy", "method name = " + method.getName());
        if (method.getName().equals("queryLocalInterface")) {
            return mBase;
        }
        return method.invoke(mOriginalBase, args);
    }
}

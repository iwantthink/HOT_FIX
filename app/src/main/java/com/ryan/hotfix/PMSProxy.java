package com.ryan.hotfix;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by renbo on 2018/5/25.
 */

public class PMSProxy implements InvocationHandler {

    private Object mBase;

    public PMSProxy(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e("PMSProxy", "methdo name = " + method.getName());


        return method.invoke(mBase, args);
    }
}

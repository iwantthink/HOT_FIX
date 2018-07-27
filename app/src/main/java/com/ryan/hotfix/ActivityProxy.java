package com.ryan.hotfix;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by renbo on 2018/5/22.
 */

public class ActivityProxy implements InvocationHandler {

    private Object mBase;

    public ActivityProxy(Object base) {
        mBase = base;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d("ActivityProxy", "hello ? " + method.getName() + ",do something");
        if (method.getName().equals("startActivity")) {

        }

        return method.invoke(mBase, args);
    }
}

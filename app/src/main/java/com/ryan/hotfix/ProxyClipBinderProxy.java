package com.ryan.hotfix;

import android.content.ClipData;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by renbo on 2018/5/24.
 */

public class ProxyClipBinderProxy implements InvocationHandler {

    private Object mBase;

    public ProxyClipBinderProxy(Object base) {
        mBase = base;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d("ProxyClipBinderProxy", "method name = " + method.getName());
        if (method.getName().equals("getPrimaryClip")) {
            ClipData data = (ClipData) method.invoke(mBase, args);
            ClipData.Item item = data.getItemAt(0);
            return new ClipData(data.getDescription().getLabel(),
                    new String[]{}, new ClipData.Item("you are hooked:" + item.getText().toString()));
        }
        return method.invoke(mBase, args);
    }
}

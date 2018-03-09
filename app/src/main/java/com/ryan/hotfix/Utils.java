package com.ryan.hotfix;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import dalvik.system.DexClassLoader;
import luck.ryan.ISayHello;

/**
 * Created by renbo on 2018/3/9.
 */

public class Utils {

    public static final String TAG = Utils.class.getSimpleName();

    public static void testHotFix(Context context) {

        // 获取到包含 class.dex 的 jar 包文件
        final File jarFile =
                new File(Environment.
                        getExternalStorageDirectory().getPath()
                        + File.separator + "HMT_TEST" + File.separator + "hello.jar");
        if (!jarFile.exists()) {
            return;
        }

        // 如果没有读权限,确定你在 AndroidManifest 中是否声明了读写权限

        if (!jarFile.exists()) {
            Log.e(TAG, "sayhello_dex.jar not exists");
            return;
        }

        Log.d(TAG, context.getClassLoader().getClass().getSimpleName());

        // getCodeCacheDir() 方法在 API 21 才能使用,实际测试替换成 getExternalCacheDir() 等也是可以的
        // 只要有读写权限的路径均可
        DexClassLoader dexClassLoader =
                new DexClassLoader(jarFile.getAbsolutePath(),
                        context.getExternalCacheDir().getAbsolutePath(),
                        null,
                        context.getClassLoader());

        Log.d(TAG, "testHotFix cacheDir = " + context.getExternalCacheDir().getAbsolutePath());
        try {
            // 加载 HelloJava 类
            Class clazz = dexClassLoader.loadClass("luck.ryan.HelloJava");
            Log.d(TAG, "dex parent name = " +
                    "" + dexClassLoader.getParent().getClass().getSimpleName());
            // 强转成 ISayHello, 注意 ISayHello 的包名需要和 jar 包中的一致
            ISayHello iSayHello = (ISayHello) clazz.newInstance();
            Log.d(TAG, "testHotFix = " + iSayHello.say());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}

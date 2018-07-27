package com.ryan.hotfix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by renbo on 2018/5/30.
 */

public class DexUtils {

    public static void loadDex(Context context) {

        File dexFile = new File("file:///android_asset/dex.jar");
        String optimizeDir = context.getFilesDir().getAbsolutePath() + File.separator + "optimize_dex";// data/data/包名/files/optimize_dex（这个必须是自己程序下的目录）
        File fopt = new File(optimizeDir);
        if (!fopt.exists()) {
            fopt.mkdirs();
            try {
                fopt.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // 1.加载应用程序的dex
        PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
        // 2.加载指定的修复的dex文件
        DexClassLoader dexLoader = new DexClassLoader(
                dexFile.getAbsolutePath(),// 修复好的dex（补丁）所在目录
                fopt.getAbsolutePath(),// 存放dex的解压目录（用于jar、zip、apk格式的补丁）
                null,// 加载dex时需要的库
                pathLoader// 父类加载器
        );

        try {
            Class helloJava_Class = dexLoader.loadClass("luck.ryan.HelloJava");
            Object helloJar = helloJava_Class.newInstance();

            Method say_Method = helloJava_Class.getDeclaredMethod("say");
            say_Method.setAccessible(true);
            String result = (String) say_Method.invoke(helloJar, new Object[]{});
            Log.d("DexUtils", result);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

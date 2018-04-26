package com.ryan.hotfix;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

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

    HashMap<String, LoadedResource> mResources = new HashMap<>();

    public LoadedResource getInstalledResource(Context parentContext, String packageName) {
        LoadedResource resource = mResources.get(packageName);  // 先从缓存中取, 没有就去加载
        if (resource == null) {
            try {
                /**
                 * 根据包名获取到已安装应用的Context
                 */
                Context context = parentContext.createPackageContext(packageName,
                        Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                resource = new LoadedResource();
                resource.packageName = packageName;
                resource.resources = context.getResources();
                resource.classLoader = context.getClassLoader();
                mResources.put(packageName, resource);  // 得到结果缓存起来
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resource;
    }

    /**
     * 获取资源ID
     *
     * @param packageName 包名
     * @param type        对应的资源类型, drawable mipmap等
     * @param fieldName
     * @return
     */
    public int getResourceID(Context context, String packageName, String type, String fieldName) {

        int resID = 0;
        // 获取已安装APK的资源
        LoadedResource installedResource = getInstalledResource(context, packageName);
        if (installedResource != null) {
            // 根据匿名内部类的命名, 拼写出R文件的包名+类名
            String rClassName = packageName + ".R$" + type;
            try {
                //  加载R文件
                Class cls = installedResource.classLoader.loadClass(rClassName);
                //  反射获取R文件对应资源名的ID
                resID = (Integer) cls.getField(fieldName).get(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "resource is null:" + packageName);
        }
        return resID;
    }

    public class LoadedResource {
        public Resources resources;
        public String packageName;
        public ClassLoader classLoader;
    }


}

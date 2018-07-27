package com.ryan.hotfix;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.RobustCallBack;
import com.meituan.robust.patch.annotaion.Modify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvTitle = findViewById(R.id.tv_title);
        mContext = MainActivity.this;
    }


    @Modify
    public String getName(String name) {
        Log.d("MainActivity", "bbb =" + name);
        return "bbb =" + name;
    }

    public void sample(View v) {
            startActivity(new Intent(MainActivity.this,SampleActivity.class));
    }


    private void initRobust() {
        new PatchExecutor(getApplicationContext(), new PatchManipulateImp(), new RobustCallBack() {
            @Override
            public void onPatchListFetched(boolean result, boolean isNet, List<Patch> patches) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPatchListFetched", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPatchFetched(boolean result, boolean isNet, Patch patch) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPatchFetched", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPatchApplied(boolean result, Patch patch) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onPatchApplied", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void logNotify(String log, String where) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "logNotify", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void exceptionNotify(final Throwable throwable, final String where) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "exceptionNotify", Toast.LENGTH_SHORT).show();
                        mTvTitle.setText("throwable.getMessage() = " + throwable.getMessage()
                                + "\n where = " + where);
                    }
                });

            }
        }).start();
    }

    TextView mTvTitle;

    public void load(View view) {
//        initRobust();
//        hookPacakges();
//        Toast.makeText(mContext, stringFromJNI(), Toast.LENGTH_SHORT).show();

//        startActivity(new Intent(
//                MainActivity.this,
//                MainActivity.class));
    }

    public void add(View view) {
//        String str = getName("abcdeg");
//        mTvTitle.setText(str);


        File apkFile = new File(
                getApplicationContext().getFilesDir().getAbsolutePath() + "/debug.apk");
        if (apkFile.exists()) {
            hookmPackages(apkFile);
            Toast.makeText(mContext, "execute hook", Toast.LENGTH_SHORT).show();
//            hookAMSBEFORE26();
            hookHandler();
//            hookDexPathList(MainActivity.this, apkFile);
        } else {
            Toast.makeText(mContext, "apk file not exist,execute copy process", Toast.LENGTH_SHORT).show();
            copyFilesFassets(MainActivity.this,
                    "debug.apk",
                    getApplicationContext().getFilesDir().getAbsolutePath() +
                            "/debug.apk");
        }

//        hookPMS();
//        hookPacakges();

    }

    private void hookPMS() {
        try {
            //ActivityThread
            Class activityThread_class = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread_field = activityThread_class.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread_field.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThread_field.get(null);

            //这里获取到的是ActivityThread. sPackageManager     是Binder代理对象
            Field sPackageManager_field = activityThread_class.getDeclaredField("sPackageManager");
            sPackageManager_field.setAccessible(true);
            Object sPackageManager = sPackageManager_field.get(null);


            //动态代理
            Object newPackageManager = Proxy.newProxyInstance(sPackageManager.getClass().getClassLoader(),
                    new Class[]{IBinder.class, IInterface.class,
                            Class.forName("android.content.pm.IPackageManager")},
                    new PMSProxy(sPackageManager));

            PackageManager packageManager = getPackageManager();
            Field mPM_field = packageManager.getClass().getDeclaredField("mPM");
            mPM_field.setAccessible(true);
            mPM_field.set(packageManager, newPackageManager);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ActivityThread.mPackages 并打印
     */
    private void hookPacakges() {
        try {
            //ActivityThread
            Class activityThread_class = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread_field = activityThread_class.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread_field.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThread_field.get(null);

            Field mPackages_field = activityThread_class.getDeclaredField("mPackages");
            mPackages_field.setAccessible(true);
            ArrayMap<String, Object> mPackages = (ArrayMap<String, Object>) mPackages_field.get(sCurrentActivityThread);

            if (mPackages == null) {
                Toast.makeText(mContext, "mPackages is null", Toast.LENGTH_SHORT).show();
                return;
            }

            Iterator<String> iterator = mPackages.keySet().iterator();
            while (iterator.hasNext()) {
                String xxx = iterator.next();
                Log.d("MainActivity", "pkg name = " + xxx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hookmPackages(File apkFile) {
        try {
            //获取到mPackages
            Class activityThread_class = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread_field = activityThread_class.
                    getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread_field.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThread_field.get(null);


            Field mPackages_field = activityThread_class.
                    getDeclaredField("mPackages");
            mPackages_field.setAccessible(true);
            //ArrayMap<String, WeakReference<LoadedApk>> mPackages
            ArrayMap mPackages = (ArrayMap) mPackages_field.get(sCurrentActivityThread);

            Class compatibilityInfo = Class.forName("android.content.res.CompatibilityInfo");

            Method getPackageInfoNoCheck_method = activityThread_class.getDeclaredMethod(
                    "getPackageInfoNoCheck",
                    ApplicationInfo.class,
                    compatibilityInfo);
            getPackageInfoNoCheck_method.setAccessible(true);


            //获取ApplicationInfo,插件的
            ApplicationInfo applicationInfo = (ApplicationInfo) getLocalApplicationInfo(apkFile);

            //获取CompatibilityInfo
            Object DEFAULT_COMPATIBILITY_INFO = getCompatibilityInfo();
            //获取LoadedApk
            Object loadedApk = getPackageInfoNoCheck_method.invoke(
                    sCurrentActivityThread,
                    applicationInfo,
                    DEFAULT_COMPATIBILITY_INFO);

            //替换LoadedApk中的ClassLoader
            Field mClassLoader_Field = loadedApk.getClass().
                    getDeclaredField("mClassLoader");
            mClassLoader_Field.setAccessible(true);
            DexClassLoader dexClassLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                    getPluginOptDirPath(),
                    getPluginLibSearchPath(),
                    ClassLoader.getSystemClassLoader());

            mClassLoader_Field.set(loadedApk, dexClassLoader);
            //替换mPackages中的 loadedapk
            mPackages.put(applicationInfo.packageName, new WeakReference(loadedApk));

            mPackages_field.set(sCurrentActivityThread, mPackages);

        } catch (Exception e) {
            Log.d("MainActivity", "hookmPackages = " + e.getMessage());
        }
    }

    private Object getCompatibilityInfo() throws Exception {
        Class compatibilityInfo = Class.forName("android.content.res.CompatibilityInfo");
        Field DEFAULT_COMPATIBILITY_INFO_field = compatibilityInfo.
                getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        DEFAULT_COMPATIBILITY_INFO_field.setAccessible(true);
        return DEFAULT_COMPATIBILITY_INFO_field.get(null);
    }


    private void hookClipBoard() {
        try {
            //获取ServiceManager对象,通过其成员变量sServiceManager获取
            Class serviceManager_Class = Class.forName("android.os.ServiceManager");
            Field sServiceManager_Field = serviceManager_Class.getDeclaredField("sServiceManager");
            sServiceManager_Field.setAccessible(true);
            Object sServiceManager = sServiceManager_Field.get(null);
            //获取原始IBinder,通过反射执行ServiceManager.getService(String name)
            Method getService_Method = serviceManager_Class.getDeclaredMethod("getService", String.class);
            getService_Method.setAccessible(true);
            IBinder originalClipIBinder = (IBinder) getService_Method.invoke(sServiceManager, "clipboard");
            //Hook queryLocalInterface,令其返回 被动态代理的Binder代理对象
            IBinder clipProxy = (IBinder) Proxy.newProxyInstance(originalClipIBinder.getClass().getClassLoader(),
                    new Class[]{IBinder.class}, new OriginalClipBinderProxy(originalClipIBinder));


            //获取ServiceManager对象的sCache成员变量
            Field sCache_Field = serviceManager_Class.getDeclaredField("sCache");
            sCache_Field.setAccessible(true);
            HashMap<String, IBinder> sCache = (HashMap<String, IBinder>) sCache_Field.get(sServiceManager);

            //将被修改过的Binder存入sCache
            sCache.put(Context.CLIPBOARD_SERVICE, clipProxy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hookAMSAfter25() {
        try {
            Class activityManagerClass = ActivityManager.class;
            Field IActivityManagerSingletonField = activityManagerClass.
                    getDeclaredField("IActivityManagerSingleton");
            IActivityManagerSingletonField.setAccessible(true);
            Object IActivityManagerSingleton = IActivityManagerSingletonField.get(null);

            Class singleTonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singleTonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            //获取到了IActivityManager.即Binder.Stub.Proxy
            Object mInstance = mInstanceField.get(IActivityManagerSingleton);

            Object newInstance = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(),
                    new Class[]{IBinder.class,
                            IInterface.class,
                            Class.forName("android.app.IActivityManager")},
                    new ActivityProxy(mInstance));

            mInstanceField.set(IActivityManagerSingleton, newInstance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hookAMSBEFORE26() {

        try {
            Class acitivtyManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = acitivtyManagerNativeClass.
                    getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            Class singleTonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singleTonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            //获取到了IActivityManager.即Binder.Stub.Proxy
            Object mInstance = mInstanceField.get(gDefault);

            Object newInstance = Proxy.newProxyInstance(mInstance.getClass().getClassLoader(),
                    new Class[]{IBinder.class,
                            IInterface.class,
                            Class.forName("android.app.IActivityManager")},
                    new ActivityProxy(mInstance));

            mInstanceField.set(gDefault, newInstance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook掉ActivityThread.mH
     */
    private void hookHandler() {

        try {
            //获取ActivityThread对象
            Class class_activityThread = Class.forName("android.app.ActivityThread");
            Method method_currentActivityThread = class_activityThread.getMethod("currentActivityThread",
                    new Class[]{});
            method_currentActivityThread.setAccessible(true);
            Object activityThread = method_currentActivityThread.invoke(null, new Object[]{});


            //获取mH 这个Handler
            Field mHField = class_activityThread.getDeclaredField("mH");
            //这一步是为了绕过Java检查，而不是将修饰符修改成public
            mHField.setAccessible(true);
            Object mH = mHField.get(activityThread);


            //获取mH 中的mCallback对象
            Field field_callBack = Handler.class.getDeclaredField("mCallback");
            field_callBack.setAccessible(true);
            // 给mCallback设置回调
            field_callBack.set(mH, mDispatcher);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Handler.Callback mDispatcher = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
                Object record = msg.obj;
                try {
                    Field intentField = record.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent) intentField.get(record);
                    Log.d("mDispatcher", intent.getComponent().getClassName());
                    Log.d("mDispatcher", intent.getComponent().getPackageName());
//                    if (intent.hasExtra("TARGET_INTENT")) {
                    if (intent.getComponent().getClassName().equals("com.ryan.hotfix.MainActivity")) {
                        Intent realIntent = new Intent();
                        realIntent.setComponent(new ComponentName(
                                "com.ryan.ndksample",
                                "com.ryan.ndksample.MainActivity"));
                        intentField.set(record, realIntent);


                        Field activityInfoField = msg.obj.getClass().getDeclaredField("activityInfo");
                        activityInfoField.setAccessible(true);
                        // 根据 getPackageInfo 根据这个 包名获取 LoadedApk的信息; 因此这里我们需要手动填上, 从而能够命中缓存
                        ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(msg.obj);
                        activityInfo.applicationInfo.packageName = realIntent.getPackage() == null ?
                                realIntent.getComponent().getPackageName() : realIntent.getPackage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };

    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context     Context 使用CopyFiles类的Activity
     * @param oldFileName String  原文件名称
     * @param newPath     String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets(Context context, String oldFileName, String newPath) {
        try {
            InputStream is = context.getAssets().open(oldFileName);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        } catch (Exception e) {

            Log.e("MainActivity", e.getMessage());
        }
    }


    public Object getLocalApplicationInfo(File apkFile) {
        try {
            Class packageParser_class = Class.forName("android.content.pm.PackageParser");
            Class package_class = Class.forName("android.content.pm.PackageParser$Package");
            Class packageUserState_class = Class.forName("android.content.pm.PackageUserState");

            Method parsePackage_method = packageParser_class.
                    getDeclaredMethod("parsePackage", File.class, int.class);
            parsePackage_method.setAccessible(true);
            Object package_obj = parsePackage_method.invoke(
                    packageParser_class.newInstance(),
                    apkFile,
                    0);

            Method generateApplicationInfo_method = packageParser_class.
                    getDeclaredMethod("generateApplicationInfo",
                            package_class,
                            int.class,
                            packageUserState_class);
            generateApplicationInfo_method.setAccessible(true);
            ApplicationInfo applicationInfo = (ApplicationInfo) generateApplicationInfo_method.invoke(null,
                    package_obj, 0,
                    packageUserState_class.newInstance());
            applicationInfo.sourceDir = apkFile.getPath();
            applicationInfo.publicSourceDir = apkFile.getPath();


            return applicationInfo;
        } catch (Exception e) {
            Log.e("MainActivity", "getLocalApplicationInfo = " + e.getMessage());
        }
        return null;
    }

    public String getPluginOptDirPath() {
        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/optdir");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public String getPluginLibSearchPath() {
        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/lib");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    public void hookDexPathList(Context context, File apkFile) {
        try {
            //ActivityThread
            Class activityThread_class = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread_field = activityThread_class.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread_field.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThread_field.get(null);
            //ArrAyMap mPackages
            Field mPackages_field = activityThread_class.getDeclaredField("mPackages");
            mPackages_field.setAccessible(true);
            ArrayMap<String, Object> mPackages = (ArrayMap<String, Object>) mPackages_field.get(sCurrentActivityThread);
            //获取到了LoadedApk
            Object loadedApk = ((WeakReference) mPackages.get(context.getPackageName())).get();

            //获取到 LoadedApk 的 PathClassLoader
            Class classLoader_class = Class.forName("android.app.LoadedApk");
            Field mClassLoader_field = classLoader_class.getDeclaredField("mClassLoader");
            mClassLoader_field.setAccessible(true);
            ClassLoader classLoader = (ClassLoader) mClassLoader_field.get(loadedApk);


            //获取到 BaseDexClassLoader 的 DexPathList
            Class baseDexClassLoader_class = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathList_field = baseDexClassLoader_class.getDeclaredField("pathList");
            pathList_field.setAccessible(true);
            Object pathList = pathList_field.get(classLoader);

            // Element[] of DexPathList
            Class dexPathList_class = Class.forName("dalvik.system.DexPathList");
            Field dexElements_field = dexPathList_class.getDeclaredField("dexElements");
            dexElements_field.setAccessible(true);
            //获取到 DexPathList 的 Elements 数组
            Object[] old_dexElements = (Object[]) dexElements_field.get(pathList);

//            Class<?> element_Class = old_dexElements.getClass().getComponentType();
//            Object[] newElements = (Object[]) Array.newInstance(element_Class,
//                    old_dexElements.length + 1);
            // 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
//            Constructor element_constructor =
//                    element_Class.getDeclaredConstructor(File.class,
//                            boolean.class,
//                            File.class,
//                            DexFile.class);
//            element_constructor.setAccessible(true);
//
//            File optDexFile = getFileStreamPath("debug.dex");
//
//            Object newElement = element_constructor.newInstance(apkFile,
//                    false,
//                    apkFile,
//                    DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(),
//                            0));
//
//            Object[] toAddElementArray = new Object[]{newElement};
//
//            // 把原始的elements复制进去
//            System.arraycopy(old_dexElements, 0,
//                    newElements, 0, old_dexElements.length);
//            // 插件的那个element复制进去
//            System.arraycopy(toAddElementArray, 0,
//                    newElements, old_dexElements.length, toAddElementArray.length);
//
//            dexElements_field.set(pathList, newElements);

            DexClassLoader dexClassLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                    getPluginOptDirPath(),
                    getPluginLibSearchPath(),
                    ClassLoader.getSystemClassLoader());
            Object new_pathList = pathList_field.get(dexClassLoader);
            Object new_dexElements = dexElements_field.get(new_pathList);
            //获取到组合的 Elements 数组
            Object dexElements = combineArray(new_dexElements, old_dexElements);


            //替换掉DexPathList 中的Elements[] 数组
            dexElements_field.set(pathList, dexElements);
            //替换掉BaseDexClassLoader中的DexPathList
            pathList_field.set(classLoader, pathList);
            //替换掉LoadedApk中的 BaseDexClassLoader
            mClassLoader_field.set(loadedApk, classLoader);
            mPackages.put(context.getPackageName(), new WeakReference(loadedApk));
            //替换掉原先的ArrayMap
            mPackages_field.set(sCurrentActivityThread, mPackages);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数组合并
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> componentType = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);// 得到左数组长度（补丁数组）
        int j = Array.getLength(arrayRhs);// 得到原dex数组长度
        int k = i + j;// 得到总数组长度（补丁数组+原dex数组）
        Object result = Array.newInstance(componentType, k);// 创建一个类型为componentType，长度为k的新数组
        System.arraycopy(arrayLhs, 0, result, 0, i);
        System.arraycopy(arrayRhs, 0, result, i, j);
        return result;
    }
}

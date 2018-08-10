package com.ryan.hotfix;

import android.app.Application;
import android.content.Context;

/**
 * Created by renbo on 2018/3/9.
 */

public class App extends Application {

    public static Context sContext;


    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
//        final File jarFile =
//                new File(Environment.
//                        getExternalStorageDirectory().getPath()
//                        + File.separator + "HMT_TEST");
//
//        FixDexUtil.loadFixedDex(this, jarFile);

//        Utils.testHotFix(this);
    }
}

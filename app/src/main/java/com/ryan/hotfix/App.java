package com.ryan.hotfix;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * Created by renbo on 2018/3/9.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final File jarFile =
                new File(Environment.
                        getExternalStorageDirectory().getPath()
                        + File.separator + "HMT_TEST");

//        FixDexUtil.loadFixedDex(this, jarFile);

        Utils.testHotFix(this);
    }
}

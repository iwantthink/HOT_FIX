package com.ryan.hotfix;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        final SecureRandom random = new SecureRandom();
//        String key = "key";
//        int per = 10000000;
//        int j = 0;
//        for (int i = 0; i < 10000; i++) {
//            final String randomDeviceId = new BigInteger(64, random).toString(16);
////            Log.d("ExampleInstrumentedTest", "randomDeviceId" + randomDeviceId);
//            int hash = (key + randomDeviceId).hashCode();
////            Log.d("ExampleInstrumentedTest", "hash:" + hash);
//            hash = Math.abs(hash);
//            int curper = hash % per;
////            Log.d("ExampleInstrumentedTest", "curper:" + curper);
//            boolean inside = curper > 5000000 && curper < 6000000;
////            Log.d("ExampleInstrumentedTest", "inside:" + inside);
//            if (inside) {
//                j++;
//            }
//        }
//
//        Log.d("ExampleInstrumentedTest", "success times:" + j);
    }

}

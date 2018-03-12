package com.ryan.hotfix;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONObject;
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

        JSONObject jsonObject = new JSONObject("{\"name\":\"ryan\"}");
        String result = jsonObject.getString("name");
        Log.d("ExampleInstrumentedTest", "result1 = " + result);
        result = jsonObject.optString("name");
        Log.d("ExampleInstrumentedTest", "result2 = " + result);
        try {
            result = jsonObject.getString("jj");
            Log.d("ExampleInstrumentedTest", "result3 = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = jsonObject.optString("jj");
        Log.d("ExampleInstrumentedTest", "result4 = " + result);

    }
}

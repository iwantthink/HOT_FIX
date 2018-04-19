package com.ryan.hotfix;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.RobustCallBack;
import com.meituan.robust.patch.annotaion.Modify;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvTitle = findViewById(R.id.tv_title);

    }

    @Modify
    public String getName(String name) {
        Log.d("MainActivity", "bbb =" + name);
        return "bbb =" + name;
    }

    public void load(View view) {
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

    public void add(View view) {
        String str = getName("abcdeg");
        mTvTitle.setText(str);
    }

}

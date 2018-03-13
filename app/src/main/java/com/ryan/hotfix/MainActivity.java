package com.ryan.hotfix;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import luck.ryan.HelloJava;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "msg = " + new HelloJava().say());

        getName("abcdeg");
    }


    public void getName(String name) {
        Log.d("MainActivity", name);
    }

}

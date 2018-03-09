package com.ryan.hotfix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import luck.ryan.HelloJava;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HelloJava helloJava = new HelloJava();
        Log.d("MainActivity", "original = " + helloJava.say());
        Utils.testHotFix(MainActivity.this);
    }
}

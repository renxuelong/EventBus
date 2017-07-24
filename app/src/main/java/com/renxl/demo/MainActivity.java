package com.renxl.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.renxl.eventbus.EventBus;
import com.renxl.eventbus.Subscrible;
import com.renxl.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getInstance().register(this);
    }

    @Subscrible(threadMode = ThreadMode.PostThread)
    public void receive(String content) {
        Log.i(TAG, "receive: " + Thread.currentThread().getName());
    }

    public void onClick(View v) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}

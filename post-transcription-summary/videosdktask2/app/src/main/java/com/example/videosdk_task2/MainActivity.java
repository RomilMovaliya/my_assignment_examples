package com.example.videosdk_task2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import live.videosdk.rtc.android.VideoSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoSDK.initialize(getApplicationContext());

        new Handler().postDelayed(() -> {
            // Start the next activity
            Intent intent = new Intent(MainActivity.this, JoinActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish() to remove this activity from the back stack
        },1000);
    }
}
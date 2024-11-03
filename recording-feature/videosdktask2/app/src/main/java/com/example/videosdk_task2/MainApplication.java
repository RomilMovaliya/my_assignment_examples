package com.example.videosdk_task2;

import android.app.Application;

import live.videosdk.rtc.android.VideoSDK;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VideoSDK.initialize(getApplicationContext());
    }
}

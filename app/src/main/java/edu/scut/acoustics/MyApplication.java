package edu.scut.acoustics;

import android.app.Application;
import android.content.IntentFilter;

public class MyApplication extends Application {
    public float db_baseline = 0;

    public boolean have_headset = false;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        registerReceiver(headsetReceiver, intentFilter);
    }

    public void initialize(){
        //TODO 初始化
    }
}

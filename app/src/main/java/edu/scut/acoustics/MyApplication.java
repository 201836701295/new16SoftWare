package edu.scut.acoustics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;

public class MyApplication extends Application {
    private static Toast toast;

    public float db_baseline = 0;

    public boolean have_headset = false;

    public float[] inverseSignal;
    public float[] sampleSignal;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        registerReceiver(headsetReceiver, intentFilter);
    }

    @SuppressLint("ShowToast")
    public void show_toast(String string) {
        if (toast == null) {
            toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
        }
        toast.show();
    }

    public void initialize() throws IOException {
        inverseSignal = new float[264600];
        sampleSignal = new float[529200];
        short temp;
        AssetFileDescriptor inverseSignalAFD = getResources().openRawResourceFd(R.raw.inverse_signal);
        AssetFileDescriptor sampleSignalAFD = getResources().openRawResourceFd(R.raw.sample_signal);
        BufferedInputStream isbis = new BufferedInputStream(inverseSignalAFD.createInputStream());
        BufferedInputStream ssbis = new BufferedInputStream(sampleSignalAFD.createInputStream());
        for (int i = 0; i < 44; ++i) {
            isbis.read();
            ssbis.read();
        }
        final int SHORT_MAX = (int) Short.MAX_VALUE + 1;
        for (int i = 0; i < inverseSignal.length; ++i) {
            temp = (short) isbis.read();
            temp |= (isbis.read() << 8);
            inverseSignal[i] = (float) temp / SHORT_MAX;
        }
        try {
            isbis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sampleSignal.length; ++i) {
            temp = (short) ssbis.read();
            temp |= (ssbis.read() << 8);
            sampleSignal[i] = (float) temp / SHORT_MAX;
        }
        try {
            ssbis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

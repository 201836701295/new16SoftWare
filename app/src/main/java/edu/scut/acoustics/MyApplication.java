package edu.scut.acoustics;

import android.app.Application;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;

import java.io.BufferedInputStream;
import java.io.IOException;

public class MyApplication extends Application {
    public float db_baseline = 0;

    public boolean have_headset = false;

    public float[] inverseSignal;
    public short[] sampleSignal;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        registerReceiver(headsetReceiver, intentFilter);
    }

    public void initialize() throws IOException {
        inverseSignal = new float[264600];
        sampleSignal = new short[529200];
        int temp;
        AssetFileDescriptor inverseSignalAFD = getResources().openRawResourceFd(R.raw.inverse_signal);
        AssetFileDescriptor sampleSignalAFD = getResources().openRawResourceFd(R.raw.sample_signal);
        BufferedInputStream isbis = new BufferedInputStream(inverseSignalAFD.createInputStream());
        BufferedInputStream ssbis = new BufferedInputStream(sampleSignalAFD.createInputStream());
        for (int i = 0; i < 44; ++i) {
            isbis.read();
            ssbis.read();
        }
        for (int i = 0; i < inverseSignal.length; ++i) {

            temp = isbis.read();
            temp |= (isbis.read() << 8);
            inverseSignal[i] = temp;
        }
        try {
            isbis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sampleSignal.length; ++i) {
            temp = ssbis.read();
            temp |= (ssbis.read() << 8);
            sampleSignal[i] = (short) temp;
        }
        try {
            ssbis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

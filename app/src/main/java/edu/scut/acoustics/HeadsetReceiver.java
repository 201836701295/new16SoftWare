package edu.scut.acoustics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")) {
            MyApplication application = (MyApplication)context.getApplicationContext();
            if (0 == intent.getIntExtra("state", 0)) {
                //耳机未插入
                application.have_headset = false;
            } else if (1 == intent.getIntExtra("state", 0)) {
                //耳机已插入
                application.have_headset = true;
            }
        }
    }
}

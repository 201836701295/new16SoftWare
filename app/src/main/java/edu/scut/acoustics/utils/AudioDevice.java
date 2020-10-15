package edu.scut.acoustics.utils;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.util.Log;

public class AudioDevice {
    AudioManager audioManager;

    public AudioDevice(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean have_headset() {
        AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo v : audioDeviceInfos) {
            Log.i("AudioDevice", "have_headset: " + v.getType());
            if (v.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET || v.getType() == AudioDeviceInfo.TYPE_USB_HEADSET
                    || v.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || v.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                    || v.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                return true;
            }
        }
        return false;
    }

    public int getMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(int v) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
    }

}

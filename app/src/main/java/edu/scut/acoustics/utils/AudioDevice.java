package edu.scut.acoustics.utils;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

public class AudioDevice {
    private AudioManager audioManager = null;

    public AudioDevice(Context context){
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

    }

    public boolean have_headset(){
        AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo v: audioDeviceInfos) {
            if(v.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET){
                return true;
            }
        }
        return false;
    }

    public int getMaxVolume(){
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(int v){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v,0);
    }

}

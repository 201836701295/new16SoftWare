package edu.scut.acoustics.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class SampleMusicPlayer {
    private AudioTrack audioTrack;
    private int length;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public SampleMusicPlayer() {
        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        AudioAttributes attributes = attributeBuilder.setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        AudioFormat.Builder formatBuilder = new AudioFormat.Builder();
        AudioFormat format = formatBuilder.setSampleRate(44100).setEncoding(AudioFormat.ENCODING_PCM_8BIT).build();

        audioTrack = new AudioTrack(attributes, format, length, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    public int write(byte[] buffer, int offset, int length) {
        return audioTrack.write(buffer, 0, buffer.length);
    }

    public void play() {
        audioTrack.play();
    }

    public void stop() {
        audioTrack.pause();
        audioTrack.flush();
    }


}

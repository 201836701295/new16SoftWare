package edu.scut.acoustics.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SinWavePlayer {
    public static final int[] FREQUENTS = {250,500,1000,2000,4000,8000};
    public static final int[] DBS = new int[]{-10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85};
    private float[] pcm_data = new float[SinWave.SAMPLE_RATE];
    private AudioTrack audioTrack;
    private SinWave sinWave;

    public SinWavePlayer() {
        sinWave = new SinWave(250,0);
        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        AudioAttributes attributes = attributeBuilder.setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        AudioFormat.Builder formatBuilder = new AudioFormat.Builder();
        AudioFormat format = formatBuilder.setSampleRate(SinWave.SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_FLOAT).build();

        audioTrack = new AudioTrack(attributes, format, pcm_data.length, AudioTrack.MODE_STATIC, AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    /**
     *
     * @param hz FREQUENTS中选取
     * @param db DBS中选取
     * @param channel AudioFormat::CHANNEL_OUT_FRONT_LEFT CHANNEL_OUT_FRONT_RIGHT CHANNEL_OUT_STEREO
     */
    public void play(int hz, int db, int channel){
        sinWave.set(hz,db);
        sinWave.doFinal(pcm_data);
    }

    public void stop(){
        audioTrack.stop();
        audioTrack.release();
        audioTrack = null;
    }
}

package edu.scut.acoustics.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SinWavePlayer {
    //各个频率，250hz到8000hz，倍频
    public static final int[] FREQUENTS = {250, 500, 1000, 2000, 4000, 8000};
    //音量级
    public static final int[] DBS = new int[]{0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85};

    public static final int LEFT = AudioFormat.CHANNEL_OUT_FRONT_LEFT;

    public static final int RIGHT = AudioFormat.CHANNEL_OUT_FRONT_RIGHT;
    //pcm_float格式的音频数据，0.5秒的音频
    float[] pcm_data = new float[SinWave.SAMPLE_RATE / 2];
    //音频播放类
    AudioTrack audioTrack;
    //正弦波生产器
    SinWave sinWave;

    public SinWavePlayer() {
        sinWave = new SinWave(250, 0);
    }

    /**
     * 耗时操作
     *
     * @param hz 频率
     * @param db 音量
     */
    public void set(int hz, int db) {
        //生成正弦波
        sinWave.set(hz, db);
        sinWave.doFinal(pcm_data);
    }

    /**
     * 播放音频
     * 耗时操作
     *
     * @param channel 左右声道 AudioFormat::CHANNEL_OUT_FRONT_LEFT CHANNEL_OUT_FRONT_RIGHT CHANNEL_OUT_STEREO
     */
    public void play(int channel) {
        if (audioTrack != null) {
            audioTrack.pause();
            audioTrack.release();
            audioTrack = null;
        }
        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        AudioAttributes attributes = attributeBuilder.setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        AudioFormat.Builder formatBuilder = new AudioFormat.Builder();
        AudioFormat format = formatBuilder.setSampleRate(SinWave.SAMPLE_RATE).setChannelMask(channel)
                .setChannelIndexMask(3).setEncoding(AudioFormat.ENCODING_PCM_FLOAT).build();

        audioTrack = new AudioTrack(attributes, format, pcm_data.length * Float.BYTES, AudioTrack.MODE_STATIC, AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.flush();
        audioTrack.write(pcm_data, 0, pcm_data.length, AudioTrack.WRITE_BLOCKING);
        audioTrack.play();
    }

    public void stop() {
        if (audioTrack != null) {
            audioTrack.pause();
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }
    }
}

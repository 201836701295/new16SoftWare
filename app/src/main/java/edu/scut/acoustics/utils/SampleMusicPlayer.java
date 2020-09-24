package edu.scut.acoustics.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class SampleMusicPlayer implements AudioTrack.OnPlaybackPositionUpdateListener{
    private AudioTrack audioTrack = null;
    private OnFinishListener onFinishListener;
    private int marker;

    public int write(short[] buffer, int offset, int length) {
        if(audioTrack != null){
            return audioTrack.write(buffer, offset, length);
        }
        return -1;
    }

    public void setMarker(int marker) {
        this.marker = marker;
    }

    public void play() {
        if(audioTrack != null){
            audioTrack.pause();
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }
        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        AudioAttributes attributes = attributeBuilder.setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        AudioFormat.Builder formatBuilder = new AudioFormat.Builder();
        AudioFormat format = formatBuilder.setSampleRate(44100).setEncoding(AudioFormat.ENCODING_PCM_16BIT).build();

        int length = AudioTrack.getMinBufferSize(SinWave.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(attributes, format, length, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.setNotificationMarkerPosition(marker);
        audioTrack.setPlaybackPositionUpdateListener(this);
        audioTrack.play();

    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    private void stop() {
        if(audioTrack != null){
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    @Override
    public void onMarkerReached(AudioTrack audioTrack) {
        if(onFinishListener != null){
            onFinishListener.OnFinish();
        }
        stop();
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {

    }

    public interface OnFinishListener {
        void OnFinish();
    }

}

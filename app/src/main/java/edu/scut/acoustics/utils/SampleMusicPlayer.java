package edu.scut.acoustics.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

/**
 * 不能用，要重写
 */
public class SampleMusicPlayer implements AudioTrack.OnPlaybackPositionUpdateListener {
    private AudioTrack audioTrack = null;
    private OnFinishListener onFinishListener;
    private int marker;
    private Handler handler;

    public int write(short[] buffer, int offset, int length) {
        if (audioTrack != null) {
            return audioTrack.write(buffer, offset, length);
        }
        return -1;
    }

    public void setMarker(int marker) {
        this.marker = marker;
    }

    public void play() {
        if (audioTrack != null) {
            audioTrack.pause();
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }
        AudioAttributes.Builder attributeBuilder = new AudioAttributes.Builder();
        AudioAttributes attributes = attributeBuilder.setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        AudioFormat.Builder formatBuilder = new AudioFormat.Builder();
        AudioFormat format = formatBuilder.setSampleRate(44100).setEncoding(AudioFormat.ENCODING_PCM_16BIT).build();

        int length = AudioTrack.getMinBufferSize(SinWave.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 4;

        audioTrack = new AudioTrack(attributes, format, length, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.setNotificationMarkerPosition(marker);
        if(handler != null){
            audioTrack.setPlaybackPositionUpdateListener(this,handler);
        }

        Log.d("audioTrack", "play: " + marker);
        audioTrack.play();

    }

    public void setOnFinishListener(OnFinishListener onFinishListener, Handler handler) {
        this.onFinishListener = onFinishListener;
        this.handler = handler;
    }

    private void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    @Override
    public void onMarkerReached(AudioTrack audioTrack) {
        if (onFinishListener != null) {
            onFinishListener.onFinish();
        }
        Log.d("audio finish", "onMarkerReached: ");
        stop();
    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {

    }

    public interface OnFinishListener {
        void onFinish();
    }

}

package edu.scut.acoustics.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    MediaPlayer player;
    Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (listener != null) {
            listener.prepare_finished();
        }
        player.start();
        Log.d("player start", "onPrepared: ");
    }

    public int getDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (listener != null) {
            listener.media_finished();
        }
        stop();
    }

    public void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public int getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public void play(AssetFileDescriptor assetFileDescriptor) throws IOException {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        player = new MediaPlayer();
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA);
        player.setAudioAttributes(builder.build());
        player.setDataSource(assetFileDescriptor);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.prepareAsync();
    }

    public interface Listener {
        void prepare_finished();

        void media_finished();
    }
}

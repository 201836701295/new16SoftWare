package edu.scut.acoustics.ui.ear_test;

import android.media.AudioFormat;

import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.scut.acoustics.utils.SinWavePlayer;

public class EarViewModel extends ViewModel {
    SinWavePlayer sinWavePlayer = new SinWavePlayer();
    ExecutorService service = Executors.newSingleThreadExecutor();
    int channel = AudioFormat.CHANNEL_OUT_DEFAULT;
    Thread thread = new PlayThread();

    public EarViewModel() {
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setWave(int hz, int db) {
        sinWavePlayer.set(hz, db);
    }

    public void play() {
        service.execute(thread);
    }

    public void stop() {
        sinWavePlayer.stop();
    }

    class PlayThread extends Thread {
        @Override
        public void run() {
            sinWavePlayer.play(channel);
        }
    }
}

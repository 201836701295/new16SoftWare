package edu.scut.acoustics.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SLM {
    public static final int SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, FORMAT) * 8;
    public static final int BIT_DEPTH = 16;
    public static final int BYTE_RATE = BIT_DEPTH * SAMPLE_RATE / 8;

    private float[] audioData = new float[8192];
    private byte[] buffer = new byte[MIN_BUFFER_SIZE * 8];
    private AudioRecord recorder = null;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private Listener listener;

    public SLM(){

    }

    public void start(){
        recorder = new AudioRecord(SOURCE, SAMPLE_RATE, CHANNEL, FORMAT, MIN_BUFFER_SIZE);
    }

    public void stop(){

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void postValue(float value);
    }

    public class Calculator implements Runnable{

        @Override
        public void run() {

        }
    }
}

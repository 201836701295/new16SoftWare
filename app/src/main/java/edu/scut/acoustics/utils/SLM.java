package edu.scut.acoustics.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * dBA
 */
public class SLM {
    public static final int SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, FORMAT) * 8;
    public static final int N = MIN_BUFFER_SIZE / 2;
    public static final int A_WEIGHTING = 1;
    public static final int C_WEIGHTING = 2;
    public static final int Z_WEIGHTING = 3;

    float[] frequencies = new float[8];
    float[] dbas = new float[8];
    //short[] audioData = new short[N];
    short[] buffer = new short[(int) (0.2f * SAMPLE_RATE)];
    AudioRecord recorder = null;
    ExecutorService service = Executors.newCachedThreadPool();
    AtomicInteger mode;
    DSPMath dspMath = new DSPMath();

    MutableLiveData<Float> max;
    MutableLiveData<Float> min;
    MutableLiveData<Float> realtime;
    MutableLiveData<DB> db;
    float maxValue;
    float minValue;
    float realtimeValue;
    DB DBValue;
    Future<Void> future;
    MutableLiveData<Integer> maxAmp;
    boolean recording = false;

    public SLM() {
        mode = new AtomicInteger(A_WEIGHTING);
        maxValue = Float.NaN;
        minValue = Float.NaN;
        realtimeValue = Float.NaN;
        DBValue = new DB();
        max = new MutableLiveData<>(maxValue);
        min = new MutableLiveData<>(minValue);
        realtime = new MutableLiveData<>(realtimeValue);
        db = new MutableLiveData<>(DBValue);
        maxAmp = new MutableLiveData<>(0);
        Log.d("SLM info", "SLM: " + MIN_BUFFER_SIZE);
    }

    public LiveData<Integer> getMaxAmp() {
        return maxAmp;
    }

    public void setMode(int m) {
        mode.set(m);
    }

    float calculate(short[] x, float[] l8, float[] ff) {
        int m = mode.get();
        if (m == A_WEIGHTING) {
            return dspMath.aweight(x, l8, ff);
        }
        if (m == C_WEIGHTING) {
            return dspMath.cweight(x, l8, ff);
        }
        if (m == Z_WEIGHTING) {
            return dspMath.zweight(x, l8, ff);
        }
        return Float.NaN;
    }

    public LiveData<DB> getDb() {
        return db;
    }

    public List<MicrophoneInfo> getActiveMicrophones() throws IOException {
        if (recorder != null) {
            return recorder.getActiveMicrophones();
        }
        throw new NullPointerException("recorder not initialized");
    }

    public void start() {
        if (!recording) {
            recorder = new AudioRecord(SOURCE, SAMPLE_RATE, CHANNEL, FORMAT, MIN_BUFFER_SIZE);
            recorder.startRecording();
            Log.d("SLM", "start: ");
            future = service.submit(new Recorder());
            recording = true;
        }
    }

    public boolean isRecording() {
        return recording;
    }

    public void stop() {
        if (recording) {
            recorder.stop();
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            recorder.release();
            recorder = null;
            future = null;
            recording = false;
        }
    }

    public LiveData<Float> getMax() {
        return max;
    }

    public LiveData<Float> getMin() {
        return min;
    }

    public LiveData<Float> getRealtime() {
        return realtime;
    }

    /**
     * 线程调用
     *
     * @param realtime 实时获取的分贝值
     */
    void postRealtime(float realtime, float[] db) {
        realtimeValue = realtime;
        if (minValue > realtimeValue || Float.isNaN(minValue)) {
            minValue = realtimeValue;
            this.min.postValue(minValue);
        }
        if (maxValue < realtimeValue || Float.isNaN(maxValue)) {
            maxValue = realtimeValue;
            this.max.postValue(maxValue);
        }
        this.realtime.postValue(realtimeValue);
        System.arraycopy(db, 0, DBValue.yValue, 0, db.length);
        this.db.postValue(DBValue);
    }

    public void refresh() {
        minValue = maxValue = realtimeValue;
        max.setValue(maxValue);
        min.setValue(minValue);
    }

    public static class DB {
        public float[] yValue = new float[8];
        public float[] xAxisValue = new float[8];

        public DB() {
            Arrays.fill(yValue, Float.NaN);
        }
    }

    class Recorder implements Callable<Void> {
        final int period = (int) (0.2f * SAMPLE_RATE);
        int off, length, temp;
        int index = 0;
        int max = 0;
        float result;

        @Override
        public Void call() {
            delay();
            try {
                while (true) {
                    temp = recorder.read(buffer, 0, buffer.length);
                    Log.d("SLM", "audio data length: " + temp);
                    if (temp == 0) {
                        return null;
                    }
                    //找最大
                    for (int i = 0; i < buffer.length; i++, ++index) {
                        if (index >= period) {
                            maxAmp.postValue(max);
                            Log.d("SLM", "call: post max amp" + max);
                            max = 0;
                            index = 0;
                        }
                        if (Math.abs(buffer[i]) > max) {
                            max = Math.abs(buffer[i]);
                        }
                    }
                    result = calculate(buffer, dbas, frequencies);
                    postRealtime(result, dbas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //延迟
        void delay() {
            int delay = (int) (SAMPLE_RATE * 0.2f);
            while (delay > 0) {
                length = Math.min(delay, buffer.length);
                temp = recorder.read(buffer, 0, length);
                delay -= temp;
            }
        }
    }
}

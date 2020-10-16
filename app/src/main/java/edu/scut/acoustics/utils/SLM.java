package edu.scut.acoustics.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * dBA
 */
public class SLM {
    public static final int SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, FORMAT) * 8;
    public static final int N = 8192;

    float[] frequencies = new float[10];
    float[] dbas = new float[10];
    short[] audioData = new short[N];
    short[] buffer = new short[MIN_BUFFER_SIZE / 2];
    AudioRecord recorder = null;
    ExecutorService service = Executors.newCachedThreadPool();

    MutableLiveData<Float> max;
    MutableLiveData<Float> min;
    MutableLiveData<Float> realtime;
    MutableLiveData<DBA> dba;
    float maxValue;
    float minValue;
    float realtimeValue;
    DBA dbaValue;
    Future<Void> future;

    public SLM() {
        maxValue = 0f;
        minValue = 0f;
        realtimeValue = 0f;
        dbaValue = new DBA();
        max = new MutableLiveData<>(maxValue);
        min = new MutableLiveData<>(minValue);
        realtime = new MutableLiveData<>(realtimeValue);
        dba = new MutableLiveData<>(dbaValue);
    }

    public LiveData<DBA> getDba() {
        return dba;
    }

    public List<MicrophoneInfo> getActiveMicrophones() throws IOException {
        if (recorder != null) {
            return recorder.getActiveMicrophones();
        }
        throw new NullPointerException("recorder not initialized");
    }

    public void start() {
        if (recorder != null) {
            recorder.stop();
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            future = null;
            recorder.release();
            recorder = null;
        }
        recorder = new AudioRecord(SOURCE, SAMPLE_RATE, CHANNEL, FORMAT, MIN_BUFFER_SIZE);
        recorder.startRecording();
        Log.d("SLM", "start: ");
        future = service.submit(new Calculator());
    }

    public void stop() {
        if (recorder != null) {
            recorder.stop();
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            recorder.release();
            recorder = null;
            future = null;
        }
        future = null;
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
    void postRealtime(float realtime, float[] dbas) {
        realtimeValue = realtime;
        if (minValue > realtimeValue) {
            minValue = realtimeValue;
            this.min.postValue(minValue);
        }
        if (maxValue < realtimeValue) {
            maxValue = realtimeValue;
            this.max.postValue(maxValue);
        }
        this.realtime.postValue(realtimeValue);
        System.arraycopy(dbas, 0, dbaValue.yValue, 0, dbas.length);
        dba.postValue(dbaValue);
    }

    void initialPost(float rv, float[] dbas, float[] frequencies) {
        maxValue = minValue = realtimeValue = rv;
        min.postValue(minValue);
        max.postValue(maxValue);
        realtime.postValue(realtimeValue);
        System.arraycopy(dbas, 0, dbaValue.yValue, 0, dbas.length);
        System.arraycopy(frequencies, 0, dbaValue.xAxisValue, 0, frequencies.length);
        dba.postValue(dbaValue);
    }

    public static class DBA {
        public float[] yValue = new float[10];
        public float[] xAxisValue = new float[10];
    }

    public void refresh() {
        minValue = maxValue = realtimeValue;
        max.setValue(maxValue);
        min.setValue(minValue);
    }

    private class Calculator implements Callable<Void> {
        @Override
        public Void call() {
            try {
                DSPMath dspMath = new DSPMath();
                int off, length, temp;
                short tv1, tv2;
                float result;

                off = 0;
                length = buffer.length;
                while (off < N) {
                    temp = recorder.read(buffer, off, length);
                    if (temp == 0) {
                        return null;
                    }
                    off += temp;
                    length -= temp;
                }
                System.arraycopy(buffer, 0, audioData, 0, audioData.length);
                result = dspMath.slmfunc(audioData, dbas, frequencies);
                initialPost(result, dbas, frequencies);

                while (true) {
                    off = 0;
                    length = buffer.length;
                    while (off < N) {
                        temp = recorder.read(buffer, off, length);
                        if (temp == 0) {
                            return null;
                        }
                        off += temp;
                        length -= temp;
                    }
                    System.arraycopy(buffer, 0, audioData, 0, audioData.length);
                    result = dspMath.slmfunc(audioData, dbas, frequencies);
                    postRealtime(result, dbas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

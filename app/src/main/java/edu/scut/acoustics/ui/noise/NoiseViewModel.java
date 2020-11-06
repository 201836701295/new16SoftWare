package edu.scut.acoustics.ui.noise;

import android.media.AudioDeviceInfo;
import android.media.MicrophoneInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.scut.acoustics.utils.SLM;

public class NoiseViewModel extends ViewModel {
    LiveData<Float> max;
    LiveData<Float> min;
    LiveData<Float> realtime;
    LiveData<SLM.DB> db;
    LiveData<Integer> maxAmp;
    MutableLiveData<String> sourceType;
    MutableLiveData<Boolean> running;
    TimerTask timerTask;
    Timer timer;
    SLM slm;

    public NoiseViewModel() {
        slm = new SLM();
        max = slm.getMax();
        min = slm.getMin();
        realtime = slm.getRealtime();
        db = slm.getDb();
        sourceType = new MutableLiveData<>("");
        maxAmp = slm.getMaxAmp();
        running = new MutableLiveData<>(false);
    }

    public LiveData<Boolean> isRunning() {
        return running;
    }

    public boolean isRecording() {
        return slm.isRecording();
    }

    public LiveData<Integer> getMaxAmp() {
        return maxAmp;
    }

    public LiveData<String> getSourceType() {
        return sourceType;
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

    public LiveData<SLM.DB> getDb() {
        return db;
    }

    public void setMode(int mode) {
        slm.setMode(mode);
    }

    public void start() {
        if (timer == null) {
            slm.start();
            running.setValue(true);
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        List<MicrophoneInfo> microphoneInfos = slm.getActiveMicrophones();
                        StringBuilder builder = new StringBuilder();
                        for (MicrophoneInfo v : microphoneInfos) {
                            Log.i("micType", "micType: " + v.getType());
                            switch (v.getType()) {
                                case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                                    builder.append("音源：内置麦克风");
                                    break;
                                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                                    builder.append("音源：外接麦克风");
                                    break;
                                case AudioDeviceInfo.TYPE_USB_HEADSET:
                                    builder.append("音源：外接USB麦克风");
                                    break;
                            }
                        }
                        sourceType.postValue(builder.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 500, 1000);
        }
    }

    public void refresh() {
        slm.refresh();
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
            sourceType.setValue("");
            slm.stop();
            running.setValue(false);
        }
    }
}

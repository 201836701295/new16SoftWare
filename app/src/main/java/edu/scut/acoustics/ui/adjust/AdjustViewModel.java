package edu.scut.acoustics.ui.adjust;

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

public class AdjustViewModel extends ViewModel {
    LiveData<Float> realtime;
    MutableLiveData<String> sourceType;
    TimerTask timerTask;
    Timer timer = new Timer();
    SLM slm;

    public AdjustViewModel() {
        slm = new SLM();
        realtime = slm.getRealtime();
        sourceType = new MutableLiveData<>("");
    }

    public LiveData<String> getSourceType() {
        return sourceType;
    }

    public void start() {
        slm.start();
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        List<MicrophoneInfo> microphoneInfos = slm.getActiveMicrophones();
                        StringBuilder string = new StringBuilder();
                        for (MicrophoneInfo v : microphoneInfos) {
                            Log.i("micType", "micType: " + v.getType());
                            switch (v.getType()) {
                                case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                                    string.append("内置麦克风\n");
                                    break;
                                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                                    string.append("外接麦克风\n");
                                    break;
                                case AudioDeviceInfo.TYPE_USB_HEADSET:
                                    string.append("外接USB麦克风\n");
                                    break;
                            }
                        }
                        sourceType.postValue(string.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask, 0, 1000);
        }
    }

    public void stop() {
        timer.cancel();
        timerTask = null;
        slm.stop();
    }
}

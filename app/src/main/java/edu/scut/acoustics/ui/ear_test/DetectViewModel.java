package edu.scut.acoustics.ui.ear_test;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

import edu.scut.acoustics.utils.SLM;

public class DetectViewModel extends ViewModel {
    SLM slm;
    LiveData<Float> realtime;
    LiveData<Integer> maxAmp;
    MutableLiveData<Boolean> insist;
    TimerTask timerTask;
    Timer timer = new Timer();

    public DetectViewModel() {
        slm = new SLM();
        realtime = slm.getRealtime();
        maxAmp = slm.getMaxAmp();
        insist = new MutableLiveData<>(false);
    }

    public LiveData<Integer> getMaxAmp() {
        return maxAmp;
    }

    public LiveData<Float> getRealtime() {
        return realtime;
    }

    public LiveData<Boolean> getInsist() {
        return insist;
    }

    public void start() {
        slm.start();
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                insist.postValue(true);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 10000);
    }

    public void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
        slm.stop();
    }
}

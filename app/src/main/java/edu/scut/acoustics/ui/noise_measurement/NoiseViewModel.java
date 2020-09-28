package edu.scut.acoustics.ui.noise_measurement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.scut.acoustics.utils.SLM;

public class NoiseViewModel extends ViewModel {
    LiveData<Float> max;
    LiveData<Float> min;
    LiveData<Float> realtime;
    SLM slm;


    public NoiseViewModel() {
        slm = new SLM();
        max = slm.getMax();
        min = slm.getMin();
        realtime = slm.getRealtime();
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

    public void start(){
        slm.start();
    }

    public void refresh(){
        slm.refresh();
    }

    public void stop(){
        slm.stop();
    }
}

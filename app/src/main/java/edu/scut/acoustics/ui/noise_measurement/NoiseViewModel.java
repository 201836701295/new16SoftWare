package edu.scut.acoustics.ui.noise_measurement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.scut.acoustics.utils.SLM;

public class NoiseViewModel extends ViewModel {
    private float maxValue;
    private float minValue;
    private float realtimeValue;
    private MutableLiveData<Float> max;
    private MutableLiveData<Float> min;
    private MutableLiveData<Float> realtime;
    private SLM slm;


    public NoiseViewModel() {
        maxValue = 0f;
        minValue = 0f;
        realtimeValue = 0f;
        max = new MutableLiveData<>(maxValue);
        min = new MutableLiveData<>(minValue);
        realtime = new MutableLiveData<>(realtimeValue);
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

    public void setRealtime(float realtime) {
        if(minValue > realtime){
            minValue = realtimeValue;
            this.min.setValue(minValue);
        }
        if(maxValue < realtime){
            maxValue = realtimeValue;
            this.max.setValue(maxValue);
        }
        this.realtime.setValue(realtime);
    }

    public void postRealtime(float realtime) {
        if(minValue > realtime){
            minValue = realtimeValue;
            this.min.postValue(minValue);
        }
        if(maxValue < realtime){
            maxValue = realtimeValue;
            this.max.postValue(maxValue);
        }
        this.realtime.postValue(realtime);
    }

    public void refresh(){
        minValue = maxValue = realtimeValue;
        max.setValue(maxValue);
        min.setValue(minValue);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}

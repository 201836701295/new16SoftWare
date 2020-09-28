package edu.scut.acoustics.ui.adjust;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import edu.scut.acoustics.utils.SLM;

public class AdjustViewModel extends ViewModel {
    LiveData<Float> realtime;
    SLM slm;

    public AdjustViewModel() {
        slm = new SLM();
        realtime = slm.getRealtime();
    }

    public void start(){
        slm.start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        slm.stop();
        slm = null;
    }
}

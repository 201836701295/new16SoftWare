package edu.scut.acoustics.ui.ear_test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ResultViewModel extends AndroidViewModel {
    final int[] frequencies;
    EarTestRepository repository;
    LiveData<int[]> leftSensitivitiesLiveData;
    LiveData<int[]> rightSensitivitiesLiveData;

    public ResultViewModel(@NonNull Application application) {
        super(application);
        repository = EarTestRepository.getRepository(application);
        leftSensitivitiesLiveData = repository.getLeftSensitivitiesLiveData();
        rightSensitivitiesLiveData = repository.getRightSensitivitiesLiveData();
        frequencies = repository.getFrequencies();
    }

    public LiveData<int[]> getLeftSensitivitiesLiveData() {
        return leftSensitivitiesLiveData;
    }

    public LiveData<int[]> getRightSensitivitiesLiveData() {
        return rightSensitivitiesLiveData;
    }

    public int[] getFrequencies() {
        return frequencies;
    }
}

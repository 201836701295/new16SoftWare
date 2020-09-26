package edu.scut.acoustics.ui.experiment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChartViewModel extends ViewModel {
    private MutableLiveData<ChartInformation> waveChart;
    private MutableLiveData<ChartInformation> phaseChart;
    private MutableLiveData<ChartInformation> frequencyChart;
    private MutableLiveData<ChartInformation> powerChart;


    public ChartViewModel() {
        waveChart = new MutableLiveData<>();
        phaseChart = new MutableLiveData<>();
        frequencyChart = new MutableLiveData<>();
        powerChart = new MutableLiveData<>();
    }

    public LiveData<ChartInformation> getWaveChart() {
        return waveChart;
    }

    public void setWaveChart(ChartInformation waveChart) {
        this.waveChart.setValue(waveChart);
    }

    public LiveData<ChartInformation> getPhaseChart() {
        return phaseChart;
    }

    public void setPhaseChart(ChartInformation phaseChart) {
        this.phaseChart.setValue(phaseChart);
    }

    public LiveData<ChartInformation> getFrequencyChart() {
        return frequencyChart;
    }

    public void setFrequencyChart(ChartInformation frequencyChart) {
        this.frequencyChart.setValue(frequencyChart);
    }

    public LiveData<ChartInformation> getPowerChart() {
        return powerChart;
    }

    public void setPowerChart(ChartInformation powerChart) {
        this.powerChart.setValue(powerChart);
    }
}

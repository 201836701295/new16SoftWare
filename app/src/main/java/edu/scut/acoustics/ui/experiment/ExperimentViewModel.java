package edu.scut.acoustics.ui.experiment;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.utils.AudioPlayer;
import edu.scut.acoustics.utils.AudioRecorder;

public class ExperimentViewModel extends AndroidViewModel {
    LiveData<ChartInformation> waveChart;
    LiveData<ChartInformation> phaseChart;
    LiveData<ChartInformation> powerChart;
    MutableLiveData<ExperimentState> experimentState;
    ExperimentState state = new ExperimentState();
    AudioPlayer player;
    AudioRecorder recorder;
    ChartRepository repository;
    private ExecutorService service = Executors.newCachedThreadPool();


    public ExperimentViewModel(@NonNull Application application) {
        super(application);
        repository = new ChartRepository(application);
        MyApplication myApplication = (MyApplication) application;
        repository.setAudioData1(myApplication.inverseSignal);
        waveChart = repository.getWaveChartLiveData();
        phaseChart = repository.getPhaseChartLiveData();
        powerChart = repository.getPowerChartLiveData();
        experimentState = new MutableLiveData<>();
        experimentState.setValue(state);
        player = new AudioPlayer();
        recorder = new AudioRecorder(application);
    }

    public void setListener(AudioPlayer.Listener listener) {
        player.setListener(listener);
    }

    public void startPlay(AssetFileDescriptor assetFileDescriptor) throws IOException {
        player.play(assetFileDescriptor);
        state.state = ExperimentState.PLAYING;
        experimentState.setValue(state);
    }

    public void stopPlay() {
        player.stop();
    }

    public void startRecord() throws IOException {
        recorder.start();
    }

    public void stopRecord() throws ExecutionException, InterruptedException {
        recorder.stop();
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void dataProcess() {
        state.state = ExperimentState.PROCESSING;
        experimentState.setValue(state);
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(recorder.getFilename());
                    FileInputStream fis = new FileInputStream(file);
                    //获得音频长度
                    int length = (int) ((fis.getChannel().size() - 44) / 2);
                    //创建数组
                    float[] recordData = new float[length];
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    //跳过文件头
                    for (int i = 0; i < 44; i++) {
                        bis.read();
                    }
                    //读入音频数据，并转为float类型
                    short temp;
                    final int SHORT_MAX = (int) Short.MAX_VALUE + 1;
                    for (int i = 0; i < recordData.length; i++) {
                        temp = (short) bis.read();
                        temp |= bis.read() << 8;
                        recordData[i] = (float) temp / SHORT_MAX;
                    }
                    repository.setAudioData2(recordData);
                    repository.doFinal();
                    state.state = ExperimentState.FINISH;
                } catch (Exception e) {
                    e.printStackTrace();
                    state.state = ExperimentState.ERROR;
                } finally {
                    experimentState.postValue(state);
                    Log.i("data process", "run: finish");
                }
            }
        });
    }

    public void reset() {
        state.state = ExperimentState.IDLE;
        experimentState.setValue(state);
    }

    public void setError() {
        state.state = ExperimentState.ERROR;
        experimentState.setValue(state);
    }

    public LiveData<ExperimentState> getExperimentState() {
        return experimentState;
    }

    public LiveData<ChartInformation> getWaveChart() {
        return waveChart;
    }

    public LiveData<ChartInformation> getPhaseChart() {
        return phaseChart;
    }

    public LiveData<ChartInformation> getPowerChart() {
        return powerChart;
    }
}

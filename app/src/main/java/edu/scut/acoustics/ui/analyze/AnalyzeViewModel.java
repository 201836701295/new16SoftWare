package edu.scut.acoustics.ui.analyze;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.scut.acoustics.utils.AudioRecorder;

public class AnalyzeViewModel extends AndroidViewModel {
    AudioRecorder recorder;

    public AnalyzeViewModel(Application application) {
        super(application);
        recorder = new AudioRecorder(application);
    }

    public void start() throws IOException {
        recorder.start();
    }

    public void stop() throws ExecutionException, InterruptedException {
        recorder.stop();
    }
}

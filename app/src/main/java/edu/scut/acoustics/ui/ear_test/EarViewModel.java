package edu.scut.acoustics.ui.ear_test;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.scut.acoustics.R;
import edu.scut.acoustics.utils.SinWavePlayer;

public class EarViewModel extends AndroidViewModel {
    public final static int LEFT = 0;
    public final static int RIGHT = 1;

    EarTestRepository repository;
    SinWavePlayer sinWavePlayer = new SinWavePlayer();
    ExecutorService service = Executors.newSingleThreadExecutor();
    int side = 0;
    Thread thread = new PlayThread();
    int[] frequencies;
    int[] sensitivities;
    int i;
    MutableLiveData<Integer> volume;
    MutableLiveData<Integer> frequency;

    public EarViewModel(Application application) {
        super(application);

        repository = EarTestRepository.getInstance(application);
        volume = new MutableLiveData<>();
        frequency = new MutableLiveData<>();
        frequencies = application.getResources().getIntArray(R.array.frequency);
    }

    public void setSide(int side) {
        this.side = side;
        if (side == LEFT) {
            sensitivities = repository.getLeftSensitivities();
        }
        if (side == RIGHT) {
            sensitivities = repository.getRightSensitivities();
        }

    }

    public int[] getFrequencies() {
        return frequencies;
    }

    public int[] getSensitivities() {
        return sensitivities;
    }

    public void play() {
        service.execute(thread);
    }

    public void stop() {
        sinWavePlayer.stop();
    }

    public void show(int index) {
        i = index;
        int v = sensitivities[index];
        int f = frequencies[index];
        frequency.setValue(f);
        volume.setValue(v);
        sinWavePlayer.set(f, v);
    }

    public void upVolume() {
        if (sensitivities[i] < 85) {
            sensitivities[i] += 1;
            int v = sensitivities[i];
            int f = frequencies[i];
            volume.setValue(v);
            sinWavePlayer.set(f, v);
        }
    }

    public void downVolume() {
        if (sensitivities[i] > 0) {
            sensitivities[i] -= 1;
            int v = sensitivities[i];
            int f = frequencies[i];
            volume.setValue(v);
            sinWavePlayer.set(f, v);
        }
    }

    public LiveData<Integer> getFrequency() {
        return frequency;
    }

    public LiveData<Integer> getVolume() {
        return volume;
    }

    public void setVolume(int v) {
        sensitivities[i] = v;
        int f = frequencies[i];
        volume.setValue(v);
        sinWavePlayer.set(f, v);
    }

    class PlayThread extends Thread {
        @Override
        public void run() {
            try {
                sinWavePlayer.play(side);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

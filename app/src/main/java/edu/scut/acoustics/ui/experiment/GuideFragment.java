package edu.scut.acoustics.ui.experiment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.Timer;
import java.util.TimerTask;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentGuideBinding;

public class GuideFragment extends Fragment {
    FragmentGuideBinding binding;
    TimerTask timerTask;
    Timer timer;
    ExperimentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guide, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ExperimentViewModel.class);
        viewModel.getMaxAmp().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.audioRecordView.update(integer);
            }
        });
        viewModel.getExperimentState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                Log.d("ExperimentState", "ExperimentState: " + i);
                switch (i) {
                    case ExperimentState.IDLE:
                        binding.audioRecordView.recreate();
                        binding.progressBar.setProgress(0);
                        stopCountDown();
                        break;
                    case ExperimentState.FINISH:
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.show_outcome);
                        break;
                    case ExperimentState.PLAYING:
                        Log.d("ExperimentState", "onChanged: start count down");
                        startCountDown();
                        break;
                    case ExperimentState.PROCESSING:
                        binding.progressBar.setProgress(binding.progressBar.getMax());
                        stopCountDown();
                        break;
                }
            }
        });
        return binding.getRoot();
    }

    void startCountDown() {
        if (timer == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    binding.progressBar.setProgress(viewModel.getCurrentPosition());
                    Log.d("progress", "timerTask: " + viewModel.getCurrentPosition());
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 100);
        }
        binding.progressBar.setMax(viewModel.getDuration());
    }

    void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
        binding.audioRecordView.recreate();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCountDown();
    }
}

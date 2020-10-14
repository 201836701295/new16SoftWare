package edu.scut.acoustics.ui.experiment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Timer;
import java.util.TimerTask;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentGuideBinding;

public class GuideFragment extends Fragment {
    FragmentGuideBinding binding;
    TimerTask timerTask;
    Timer timer = new Timer();
    ExperimentViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guide, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ExperimentViewModel.class);
        return binding.getRoot();
    }

    public void startCountDown(int duration) {
        binding.progressBar.setMax(duration);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                binding.progressBar.setProgress(viewModel.getCurrentPosition());
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    public void stopCountDown() {
        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}

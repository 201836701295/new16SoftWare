package edu.scut.acoustics.ui.ear_test;

import android.media.AudioFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentLeftEarBinding;

public class LeftEarFragment extends Fragment implements View.OnClickListener {
    EarViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLeftEarBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_left_ear, container, false);
        binding.earTest.ear.setText(R.string.left_ear);
        binding.earTest.play.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(EarViewModel.class);
        viewModel.setChannel(AudioFormat.CHANNEL_OUT_FRONT_LEFT);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        viewModel.stop();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        viewModel.setWave(500, 90);
        viewModel.play();
    }
}

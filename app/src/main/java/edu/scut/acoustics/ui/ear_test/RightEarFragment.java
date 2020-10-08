package edu.scut.acoustics.ui.ear_test;

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
import edu.scut.acoustics.databinding.FragmentRightEarBinding;

public class RightEarFragment extends Fragment implements View.OnClickListener {
    EarViewModel viewModel;
    FragmentRightEarBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_right_ear, container, false);
        binding.earTest.ableHear.setOnClickListener(this);
        binding.earTest.upbtn.setOnClickListener(this);
        binding.earTest.downbtn.setOnClickListener(this);
        binding.earTest.play.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(EarViewModel.class);
        viewModel.setSide(1);
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.stop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.able_hear:
                able_hear();
                break;
            case R.id.upbtn:
                break;
            case R.id.downbtn:
                break;
            case R.id.play:
                break;
        }
    }

    void able_hear() {

    }
}

package edu.scut.acoustics.ui.ear_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentTestResultBinding;

public class TestResultFragment extends Fragment {
    FragmentTestResultBinding binding;
    ResultViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_result, container, false);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        viewModel.getLeftSensitivitiesLiveData().observe(getViewLifecycleOwner(), new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {

            }
        });
        viewModel.getRightSensitivitiesLiveData().observe(getViewLifecycleOwner(), new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {

            }
        });

        return binding.getRoot();
    }
}

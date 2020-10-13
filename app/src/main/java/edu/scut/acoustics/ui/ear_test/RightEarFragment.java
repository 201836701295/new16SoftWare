package edu.scut.acoustics.ui.ear_test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentLeftEarBinding;
import edu.scut.acoustics.utils.AudioDevice;

public class RightEarFragment extends Fragment implements View.OnClickListener {
    EarViewModel viewModel;
    FragmentLeftEarBinding binding;
    Button[] hzs;
    int current = 0;
    AudioDevice device;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_right_ear, container, false);
        binding.earTest.ableHear.setOnClickListener(this);
        binding.earTest.upbtn.setOnClickListener(this);
        binding.earTest.downbtn.setOnClickListener(this);
        binding.earTest.play.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(EarViewModel.class);
        viewModel.setSide(EarViewModel.RIGHT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        hzs = new Button[viewModel.getFrequencies().length];
        for (int i = 0; i < hzs.length; i++) {
            hzs[i] = new Button(requireContext());
            hzs[i].setText(getString(R.string.hzbtn, viewModel.getFrequencies()[i]));
            hzs[i].setOnClickListener(this);
            binding.earTest.freqcontainer.addView(hzs[i], layoutParams);
        }
        hzs[current].setEnabled(false);
        viewModel.show(current);

        viewModel.getFrequency().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    binding.earTest.frequency.setText(getString(R.string.currentfreq, integer));
                }
            }
        });
        viewModel.getVolume().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    binding.earTest.volume.setText(getString(R.string.currentdb, integer));
                    binding.earTest.seekbar.setProgress(integer);
                }
            }
        });

        binding.earTest.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewModel.setVolume(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        viewModel.getTested().observe(getViewLifecycleOwner(), new Observer<EarTestRepository.Tested>() {
            @Override
            public void onChanged(EarTestRepository.Tested tested) {
                Log.d("asdfg", "onChanged() returned: " + tested.left);
                if (tested.right == viewModel.TEST_FINISH + 1) {
                    return;
                }
                if (tested.right != viewModel.TEST_FINISH) {
                    //寻找未测试项
                    for (int i = 0; i < viewModel.getFrequencies().length; i++) {
                        if (((tested.right >> i) & 1) == 0) {
                            hzs[current].setEnabled(true);
                            current = i;
                            viewModel.show(current);
                            hzs[current].setEnabled(false);
                            break;
                        }
                    }
                    return;
                }
                if (tested.left != viewModel.TEST_FINISH && tested.left != viewModel.TEST_FINISH + 1) {
                    viewModel.resetTested();
                    //进入下一页面
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.right_to_left);
                    return;
                }
                //所有测试做完
                viewModel.resetTested();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.right_to_result);
            }
        });

        device = new AudioDevice(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.stop();
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < hzs.length; i++) {
            if (hzs[i] == view) {
                viewModel.stop();
                hzs[current].setEnabled(true);
                current = i;
                hzs[current].setEnabled(false);
                viewModel.show(current);
                return;
            }
        }
        switch (view.getId()) {
            case R.id.able_hear:
                viewModel.test(current);
                break;
            case R.id.upbtn:
                viewModel.upVolume();
                break;
            case R.id.downbtn:
                viewModel.downVolume();
                break;
            case R.id.play:
                if (!device.have_headset()) {
                    MyApplication application = (MyApplication) requireActivity().getApplication();
                    application.show_toast("请带上耳机测试");
                    return;
                }
                device.setVolume(device.getMaxVolume());
                viewModel.play();
                break;
        }
    }
}

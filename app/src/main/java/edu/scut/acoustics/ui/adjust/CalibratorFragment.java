package edu.scut.acoustics.ui.adjust;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentCalibratorBinding;

public class CalibratorFragment extends Fragment {
    FragmentCalibratorBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calibrator, container, false);
        ColorStateList colorStateList = new ColorStateList(new int[][]{{android.R.attr.state_checked, android.R.attr.state_pressed}, {}}, new int[]{Color.BLUE, Color.GRAY});
        binding.hz1000.setCheckMarkTintList(colorStateList);
        binding.hz250.setCheckMarkTintList(colorStateList);
        return binding.getRoot();
    }
}

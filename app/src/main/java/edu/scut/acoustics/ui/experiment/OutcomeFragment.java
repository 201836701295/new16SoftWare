package edu.scut.acoustics.ui.experiment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FramentOutcomeBinding;

public class OutcomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FramentOutcomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.frament_outcome,container,false);
        return binding.getRoot();
    }
}

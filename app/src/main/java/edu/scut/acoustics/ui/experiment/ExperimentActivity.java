package edu.scut.acoustics.ui.experiment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityExperimentBinding;

public class ExperimentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityExperimentBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_experiment);
    }
}

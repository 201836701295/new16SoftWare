package edu.scut.acoustics.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityMainBinding;
import edu.scut.acoustics.ui.ear_test.EarTestActivity;
import edu.scut.acoustics.ui.experiment.ExperimentActivity;
import edu.scut.acoustics.ui.noise_measurement.NoiseMeasurementActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.experiment.setOnClickListener(this);
        activityMainBinding.earTest.setOnClickListener(this);
        activityMainBinding.noiseMeasurement.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.experiment:
                start_experiment();
                break;
            case R.id.ear_test:
                start_ear_test();
                break;
            case R.id.noise_measurement:
                start_noise_measurement();
                break;
        }
    }

    public void start_experiment() {
        startActivity(new Intent(this, ExperimentActivity.class));
    }

    public void start_ear_test() {
        startActivity(new Intent(this, EarTestActivity.class));
    }

    public void start_noise_measurement() {
        startActivity(new Intent(this, NoiseMeasurementActivity.class));
    }
}

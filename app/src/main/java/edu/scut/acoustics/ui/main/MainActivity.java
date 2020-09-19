package edu.scut.acoustics.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        activityMainBinding.experiment.setOnClickListener(this);
        activityMainBinding.hearingAid.setOnClickListener(this);
        activityMainBinding.noiseMeasurement.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.experiment:
                start_experiment();
                break;
            case R.id.hearing_aid:
                start_hearing_aid();
                break;
            case R.id.noise_measurement:
                start_noise_measurement();
                break;
        }
    }

    public void start_experiment(){

    }

    public void start_hearing_aid(){

    }

    public void start_noise_measurement(){

    }
}

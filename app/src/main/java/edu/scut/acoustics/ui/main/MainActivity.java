package edu.scut.acoustics.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.util.Vector;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityMainBinding;
import edu.scut.acoustics.ui.ear_test.EarTestActivity;
import edu.scut.acoustics.ui.experiment.ExperimentActivity;
import edu.scut.acoustics.ui.noise_measurement.NoiseMeasurementActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final static int PERMISSIONS_FOR_DBA = 1;
    MyApplication application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.experiment.setOnClickListener(this);
        activityMainBinding.earTest.setOnClickListener(this);
        activityMainBinding.noiseMeasurement.setOnClickListener(this);

        application = (MyApplication)getApplication();
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
        //startActivity(new Intent(this, EarTestActivity.class));
    }

    public void start_noise_measurement() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_DBA);
        }
        else {
            startActivity(new Intent(this, NoiseMeasurementActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_FOR_DBA){
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            start_noise_measurement();
        }
    }
}

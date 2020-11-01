package edu.scut.acoustics.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityMainBinding;
import edu.scut.acoustics.databinding.ItemMainBinding;
import edu.scut.acoustics.ui.ear_test.DetectActivity;
import edu.scut.acoustics.ui.experiment.ExperimentActivity;
import edu.scut.acoustics.ui.noise.NoiseActivity;
import edu.scut.acoustics.utils.AudioDevice;

public class MainActivity extends AppCompatActivity {
    final static int PERMISSIONS_FOR_DBA = 1;
    final static int PERMISSIONS_FOR_DETECT = 2;
    MyApplication application;
    AudioDevice audioDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 10, 20, 10);

        ItemMainBinding binding1 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_main, binding.linearLayout, false);
        binding1.imageView.setImageResource(R.drawable.frequency_response);
        binding1.title.setText(R.string.experiment);
        binding1.description.setText(R.string.experiment_description);
        binding1.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_experiment();
            }
        });

        ItemMainBinding binding2 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_main, binding.linearLayout, false);
        binding2.imageView.setImageResource(R.drawable.noise_measure);
        binding2.title.setText(R.string.noise_measurement);
        binding2.description.setText(R.string.noise_measurement_description);
        binding2.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_noise_measurement();
            }
        });

        ItemMainBinding binding3 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_main, binding.linearLayout, false);
        binding3.imageView.setImageResource(R.drawable.ear_test);
        binding3.title.setText(R.string.ear_test);
        binding3.description.setText(R.string.ear_test_description);
        binding3.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_ear_test();
            }
        });

        binding.linearLayout.addView(binding1.getRoot(), layoutParams);
        binding.linearLayout.addView(binding2.getRoot(), layoutParams);
        binding.linearLayout.addView(binding3.getRoot(), layoutParams);

        application = (MyApplication) getApplication();
        audioDevice = new AudioDevice(this);
    }

    public void start_experiment() {
        startActivity(new Intent(this, ExperimentActivity.class));
    }

    public void start_ear_test() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_DETECT);
        } else {
            //startActivity(new Intent(this, EarTestActivity.class));
            startActivity(new Intent(this, DetectActivity.class));
        }
    }

    public void start_noise_measurement() {
        //if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
        //!= PackageManager.PERMISSION_GRANTED) {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_DBA);
        //} else {
        //startActivity(new Intent(this, NoiseMeasurementActivity.class));
        startActivity(new Intent(this, NoiseActivity.class));
        //}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_FOR_DBA) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            start_noise_measurement();
        }
        if (requestCode == PERMISSIONS_FOR_DETECT) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            start_ear_test();
        }
    }
}

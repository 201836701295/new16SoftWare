package edu.scut.acoustics.ui.noise_measurement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityNoiseMeasurementBinding;
import edu.scut.acoustics.ui.adjust.AdjustActivity;

public class NoiseMeasurementActivity extends AppCompatActivity implements View.OnClickListener {
    final static int PERMISSIONS_FOR_DBA = 1;

    ActivityNoiseMeasurementBinding binding;
    NoiseViewModel viewModel;
    DecimalFormat format = new DecimalFormat("###0.00");
    float baseline;
    MyApplication application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_noise_measurement);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        viewModel = new ViewModelProvider(this).get(NoiseViewModel.class);
        viewModel.getMax().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.max.setText(getString(R.string.max_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getMin().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.min.setText(getString(R.string.min_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getRealtime().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.real.setText(getString(R.string.real_time_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getSourceType().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.sourceType.setText(s);
            }
        });

        binding.refresh.setOnClickListener(this);
        binding.adjust.setOnClickListener(this);
        application = (MyApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        baseline = getSharedPreferences(getString(R.string.sharedpreferences), MODE_PRIVATE)
                .getFloat(getString(R.string.baseline), 0.0f);
        viewModel.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.adjust.getId()) {
            start_adjust();
        }
        if (view.getId() == binding.refresh.getId()) {
            refresh();
        }
    }

    void refresh() {
        viewModel.refresh();
    }

    public void start_adjust() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_DBA);
        } else {
            startActivity(new Intent(this, AdjustActivity.class));
        }
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
            start_adjust();
        }
    }
}

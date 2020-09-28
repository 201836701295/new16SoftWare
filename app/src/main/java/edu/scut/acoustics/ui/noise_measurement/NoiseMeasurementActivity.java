package edu.scut.acoustics.ui.noise_measurement;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityNoiseMeasurementBinding;
import edu.scut.acoustics.ui.adjust.AdjustActivity;

public class NoiseMeasurementActivity extends AppCompatActivity implements View.OnClickListener {
    static final String unit = "dBA";

    ActivityNoiseMeasurementBinding binding;
    NoiseViewModel viewModel;
    DecimalFormat format = new DecimalFormat("###.##");
    float baseline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_noise_measurement);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        baseline = getSharedPreferences(getResources().getString(R.string.sharedpreferences), MODE_PRIVATE)
                .getFloat(getResources().getString(R.string.baseline), 0.0f);

        viewModel = new ViewModelProvider(this).get(NoiseViewModel.class);
        viewModel.getMax().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                String temp = getResources().getString(R.string.max_db) + "\n" + format.format(aFloat + baseline) + unit;
                binding.max.setText(temp);
            }
        });
        viewModel.getMin().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                String temp = getResources().getString(R.string.min_db) + "\n" + format.format(aFloat + baseline) + unit;
                binding.min.setText(temp);
            }
        });
        viewModel.getRealtime().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                String temp = getResources().getString(R.string.real_time_db) + "\n" + format.format(aFloat + baseline) + unit;
                binding.real.setText(temp);
            }
        });

        binding.refresh.setOnClickListener(this);
        binding.adjust.setOnClickListener(this);

        new Thread() {
            @Override
            public void run() {
                viewModel.start();
            }
        }.start();
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

    void start_adjust() {
        startActivity(new Intent(this, AdjustActivity.class));
    }

    void refresh() {
        viewModel.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stop();
    }
}

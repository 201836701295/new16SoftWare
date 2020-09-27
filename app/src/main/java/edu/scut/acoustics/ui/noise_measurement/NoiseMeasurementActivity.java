package edu.scut.acoustics.ui.noise_measurement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityNoiseMeasurementBinding;
import edu.scut.acoustics.ui.adjust.AdjustActivity;

public class NoiseMeasurementActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityNoiseMeasurementBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_noise_measurement);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
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

    public void start_adjust() {
        startActivity(new Intent(this, AdjustActivity.class));
    }

    public void refresh() {

    }

}

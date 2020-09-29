package edu.scut.acoustics.ui.adjust;

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
import edu.scut.acoustics.databinding.ActivityAdjustBinding;

public class AdjustActivity extends AppCompatActivity implements View.OnClickListener {
    static final String unit = "dBA";

    DecimalFormat format = new DecimalFormat("##0.00");
    float baseline;
    float realtime = 0.0f;
    AdjustViewModel viewModel;
    String sharedpreferences;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityAdjustBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_adjust);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        binding.adjust.setOnClickListener(this);

        sharedpreferences = getResources().getString(R.string.sharedpreferences);
        key = getResources().getString(R.string.baseline);

        baseline = getSharedPreferences(sharedpreferences, MODE_PRIVATE).getFloat(key, 0.0f);

        viewModel = new ViewModelProvider(this).get(AdjustViewModel.class);
        viewModel.realtime.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                realtime = aFloat;
                String string = getResources().getString(R.string.real_time_db) + "\n" + format.format(aFloat + baseline) + unit;
                binding.real.setText(string);
            }
        });

        new Thread() {
            @Override
            public void run() {
                viewModel.start();
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        baseline += (94f - realtime);
        getSharedPreferences(sharedpreferences, MODE_PRIVATE).edit().putFloat(key, baseline).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stop();
    }
}

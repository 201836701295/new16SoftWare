package edu.scut.acoustics.ui.adjust;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
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

        sharedpreferences = getString(R.string.sharedpreferences);
        key = getString(R.string.baseline);

        viewModel = new ViewModelProvider(this).get(AdjustViewModel.class);
        viewModel.realtime.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                realtime = aFloat;
                binding.real.setText(getString(R.string.real_time_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getSourceType().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.sourceType.setText(s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        baseline = getSharedPreferences(sharedpreferences, MODE_PRIVATE).getFloat(key, 0.0f);
        viewModel.start();
        Log.i("AdjustActivity", "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stop();
        Log.i("AdjustActivity", "onPause: ");
    }

    @Override
    public void onClick(View view) {
        baseline = (94f - realtime);
        getSharedPreferences(sharedpreferences, MODE_PRIVATE).edit().putFloat(key, baseline).apply();
    }
}

package edu.scut.acoustics.ui.adjust;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityAdjustBinding;

public class AdjustActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, NumberPicker.OnValueChangeListener {
    DecimalFormat format = new DecimalFormat("##0.00");
    float baseline;
    float realtime = 0.0f;
    float target = 94f;
    AdjustViewModel viewModel;
    String sharedpreferences;
    String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityAdjustBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_adjust);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        binding.adjust.setOnClickListener(this);
        binding.numberPicker.setMaxValue(110);
        binding.numberPicker.setMinValue(40);
        binding.numberPicker.setValue(94);
        binding.numberPicker.setOnValueChangedListener(this);

        sharedpreferences = getString(R.string.sharedpreferences);
        key = getString(R.string.baseline);

        viewModel = new ViewModelProvider(this).get(AdjustViewModel.class);
        viewModel.getTargetLiveData().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.title.setText(getString(R.string.adjust_tip, aFloat.intValue()));
            }
        });
        viewModel.realtime.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                realtime = aFloat;
                binding.real.setText(getString(R.string.real_time_db, getString(R.string.dBA, format.format(aFloat + baseline))));
            }
        });
        viewModel.getSourceType().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.sourceType.setText(s);
            }
        });
        binding.editTextNumberSigned.addTextChangedListener(this);
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
        baseline = target - realtime;
        getSharedPreferences(sharedpreferences, MODE_PRIVATE).edit().putFloat(key, baseline).apply();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 1 && s.charAt(0) == '-') {
            return;
        }
        target = Float.parseFloat(s.toString());
        viewModel.setTarget(target);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
        target = newValue;
        viewModel.setTarget(target);
    }
}

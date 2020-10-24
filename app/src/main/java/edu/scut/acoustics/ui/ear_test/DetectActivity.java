package edu.scut.acoustics.ui.ear_test;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityDetectBinding;

public class DetectActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDetectBinding binding;
    DetectViewModel viewModel;
    float baseline;
    DecimalFormat format = new DecimalFormat("###0.00");
    int unit = R.string.dBA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detect);
        viewModel = new ViewModelProvider(this).get(DetectViewModel.class);

        viewModel.getMaxAmp().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.audioRecordView.update(integer);
            }
        });
        viewModel.getRealtime().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.slmText.setText(getString(R.string.real_time_db, getString(unit, format.format(aFloat + baseline))));
                if (aFloat + baseline > 40f) {
                    binding.btnEnter.setEnabled(false);
                    binding.btnEnter.setText(R.string.env_loud);
                } else {
                    binding.btnEnter.setEnabled(true);
                    binding.btnEnter.setText(R.string.env_normal);
                }
            }
        });
        viewModel.getInsist().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.insist.setVisibility(View.VISIBLE);
                    binding.insist.setEnabled(true);
                }
            }
        });
        binding.btnEnter.setOnClickListener(this);
        binding.insist.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        baseline = getSharedPreferences(getString(R.string.sharedpreferences), MODE_PRIVATE)
                .getFloat(getString(R.string.baseline), 0.0f);
        viewModel.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stop();
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, EarTestActivity.class));
        finish();
    }
}

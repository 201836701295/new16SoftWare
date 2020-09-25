package edu.scut.acoustics.ui.adjust;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityAdjustBinding;

public class AdjustActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAdjustBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_adjust);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        binding.adjust.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}

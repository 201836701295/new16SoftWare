package edu.scut.acoustics.ui.analyze;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityAnalyzeBinding;
import edu.scut.acoustics.utils.AudioRecorder;

public class AnalyzeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityAnalyzeBinding binding;
    AudioRecorder recorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_analyze);
    }

    @Override
    public void onClick(View view) {

    }
}

package edu.scut.acoustics.ui.main;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.scut.acoustics.R;

public class MainActivity extends AppCompatActivity {
    Button btn_experiment = null;
    Button btn_noise_measurement = null;
    Button btn_hearing_aid = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_experiment = findViewById(R.id.experiment);
        //TODO
    }
}

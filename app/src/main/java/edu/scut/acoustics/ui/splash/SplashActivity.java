package edu.scut.acoustics.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivitySplashBinding;
import edu.scut.acoustics.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private Handler handler = new Handler();
    private Runnable runnable = null;
    private Button skip = null;
    private CountDownTimer countDownTimer = new CountDownTimer(3200, 1000) {
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onTick(long millisUntilFinished) {
            skip.setText(getString(R.string.skip) + " " + String.format("%ds",millisUntilFinished/1000));
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onFinish() {
            skip.setText(getString(R.string.skip) + " " + String.format("%ds",0));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        activitySplashBinding.skip.setOnClickListener(this);
        skip = activitySplashBinding.skip;

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },3200);
        countDownTimer.start();
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        if(handler != null && runnable != null){
            handler.removeCallbacks(runnable);
        }
    }
}

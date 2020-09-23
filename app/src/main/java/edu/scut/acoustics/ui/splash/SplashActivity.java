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

import java.util.Objects;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivitySplashBinding;
import edu.scut.acoustics.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity{
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定页面
        ActivitySplashBinding activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        //隐藏ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //初始化
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyApplication application = (MyApplication) getApplication();
                application.initialize();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1);
    }

}

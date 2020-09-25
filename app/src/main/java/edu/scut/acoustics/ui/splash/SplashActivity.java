package edu.scut.acoustics.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.io.IOException;
import java.util.Objects;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定页面
        DataBindingUtil.setContentView(this, R.layout.activity_splash);
        //隐藏ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);

        //初始化
        new Thread(){
            @Override
            public void run() {
                MyApplication application = (MyApplication) getApplication();
                try {
                    application.initialize();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}

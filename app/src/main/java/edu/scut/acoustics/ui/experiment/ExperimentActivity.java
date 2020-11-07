package edu.scut.acoustics.ui.experiment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityExperimentBinding;
import edu.scut.acoustics.utils.AudioDevice;
import edu.scut.acoustics.utils.AudioPlayer;

public class ExperimentActivity extends AppCompatActivity implements View.OnClickListener {
    final static int PERMISSIONS = 1;
    AudioDevice device;
    ActivityExperimentBinding binding;
    ExperimentViewModel viewModel;
    MyApplication application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UI初始化
        binding = DataBindingUtil.setContentView(this, R.layout.activity_experiment);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        //设置点击监听
        binding.button.setOnClickListener(this);
        //初始化
        device = new AudioDevice(getApplicationContext());
        viewModel = new ViewModelProvider(this).get(ExperimentViewModel.class);
        viewModel.setListener(new AudioPlayer.Listener() {
            @Override
            public void prepare_finished() {
                try {
                    viewModel.setExperimentState(ExperimentState.PLAYING);
                    Log.d("ExperimentState", "prepare_finished: ");
                    viewModel.startRecord();
                } catch (IOException e) {
                    viewModel.setExperimentState(ExperimentState.ERROR);
                    e.printStackTrace();
                }
            }

            @Override
            public void media_finished() {
                try {
                    viewModel.stopRecord();
                    viewModel.setExperimentState(ExperimentState.PROCESSING);
                    viewModel.dataProcess();
                } catch (ExecutionException | InterruptedException e) {
                    viewModel.setExperimentState(ExperimentState.ERROR);
                    e.printStackTrace();
                }
            }
        });

        viewModel.getExperimentState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                switch (i) {
                    case ExperimentState.IDLE:
                    case ExperimentState.FINISH:
                        binding.button.setEnabled(true);
                        binding.progress.setVisibility(View.INVISIBLE);
                        break;
                    case ExperimentState.ERROR:
                        binding.button.setEnabled(true);
                        application.show_toast(R.string.experiment_error);
                        binding.progress.setVisibility(View.INVISIBLE);
                        break;
                    case ExperimentState.PREPARING:
                        binding.button.setEnabled(false);
                    case ExperimentState.PLAYING:
                        binding.button.setEnabled(false);
                        application.show_toast(R.string.start_to_play);
                        binding.progress.setVisibility(View.INVISIBLE);
                        break;
                    case ExperimentState.PROCESSING:
                        binding.button.setEnabled(false);
                        application.show_toast(R.string.start_to_process);
                        binding.progress.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        application = (MyApplication) getApplication();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stopPlay();
        try {
            viewModel.stopRecord();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        viewModel.setExperimentState(ExperimentState.IDLE);
        binding.button.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.shutdown();
    }

    //检查权限并请求权限
    void permission() {
        Vector<String> vector = new Vector<>();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            vector.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            vector.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            vector.add(Manifest.permission.RECORD_AUDIO);
        }

        if (vector.size() > 0) {
            String[] permissions = vector.toArray(new String[vector.size()]);
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS);
        } else {
            start_experiment();
        }
    }

    //检查权限申请情况
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            //获得必要权限后启动实验
            start_experiment();
        }
    }

    //开始实验
    void start_experiment() {
        viewModel.setExperimentState(ExperimentState.PREPARING);
        //声音最大
        device.setVolume(device.getMaxVolume());
        //设置播放事件接口
        try {
            //开始播放
            viewModel.startPlay(getResources().openRawResourceFd(R.raw.sample_signal));
        } catch (IOException e) {
            viewModel.setExperimentState(ExperimentState.ERROR);
            e.printStackTrace();
        }
    }

    void test_experiment() {
        viewModel.setExperimentState(ExperimentState.PROCESSING);
        viewModel.setAudioData2(application.sampleSignal);
    }

    //@Override
    //public void onBackPressed() {
    //    finish();
    //}

    @Override
    public void onClick(final View view) {

        //检查权限
        permission();
        //test_experiment();
    }
}

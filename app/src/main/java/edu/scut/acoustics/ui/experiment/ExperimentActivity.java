package edu.scut.acoustics.ui.experiment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityExperimentBinding;
import edu.scut.acoustics.utils.AudioPlayer;
import edu.scut.acoustics.utils.AudioRecorder;

public class ExperimentActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int PERMISSIONS = 1;
    private AudioRecorder recorder;
    private AudioPlayer player;
    private ActivityExperimentBinding binding;
    private ExecutorService service = Executors.newCachedThreadPool();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UI初始化
        binding = DataBindingUtil.setContentView(this, R.layout.activity_experiment);
        GuideFragment guideFragment = new GuideFragment();
        getSupportFragmentManager().beginTransaction().add(binding.frame.getId(), guideFragment).commit();
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        //设置点击监听
        binding.button.setOnClickListener(this);

        //初始化
        recorder = new AudioRecorder(this);
        player = new AudioPlayer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    //检查权限并请求权限
    private void permission() {
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
            return;
        }
        start_experiment();
    }

    //检查权限申请情况
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    MyApplication application = (MyApplication) getApplication();
                    application.show_toast("你拒绝提供权限");
                    return;
                }
            }
            start_experiment();
        }
    }

    //开始实验
    private void start_experiment() {
        binding.button.setEnabled(false);
        //设置播放事件接口
        player.setListener(new AudioPlayer.Listener() {
            //准备完成时调用
            @Override
            public void prepare_finished() {
                try {
                    recorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //播放结束时调用
            @Override
            public void media_finished() {
                Log.d("call media_finished", "media_finished: ");
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            recorder.stop();
                            Log.d("recorder stop", "media_finished: ");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //显示实验结果
                                    show_outcome();
                                }
                            });
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        try {
            //开始播放
            player.play(getResources().openRawResourceFd(R.raw.sample_signal));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //显示结果
    public void show_outcome() {
        Fragment fragment = new OutcomeFragment();
        getSupportFragmentManager().beginTransaction().replace(binding.frame.getId(), fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onClick(final View view) {
        //检查权限
        permission();
    }

}

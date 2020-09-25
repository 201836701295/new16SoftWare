package edu.scut.acoustics.ui.experiment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import edu.scut.acoustics.utils.AudioRecorder;
import edu.scut.acoustics.utils.SampleMusicPlayer;

public class ExperimentActivity extends AppCompatActivity implements View.OnClickListener {
    private AudioRecorder recorder;
    private SampleMusicPlayer player;
    private ActivityExperimentBinding binding;
    private ExecutorService service = Executors.newCachedThreadPool();
    private WriterTask writerTask = new WriterTask();
    private Handler handler = new Handler(Looper.getMainLooper());
    private final static int PERMISSIONS = 1;

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
        player = new SampleMusicPlayer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void permission(){
        Vector<String> vector =new Vector<>();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS){
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED){
                    MyApplication application = (MyApplication)getApplication();
                    application.show_toast("你拒绝提供权限");
                    return;
                }
            }
            start_experiment();
        }
    }

    private void start_experiment(){
        binding.button.setEnabled(false);
        try {
            //启动录音
            recorder.start();
            //设置播放结束位置
            MyApplication application = (MyApplication) getApplication();
            player.setMarker(application.sampleSignal.length / 4);
            //设置播放结束接口
            player.setOnFinishListener(new SampleMusicPlayer.OnFinishListener() {
                @Override
                public void onFinish() {
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                recorder.stop();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        show_outcome();
                                    }
                                });
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        recorder.stop();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },handler);
            //开始播放
            player.play();
            //启动写入线程
            service.execute(writerTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //启动录音
                    recorder.start();
                    //设置播放结束位置
                    MyApplication application = (MyApplication) getApplication();
                    player.setMarker(application.sampleSignal.length - 1);
                    //设置播放结束接口
                    player.setOnFinishListener(new SampleMusicPlayer.OnFinishListener() {
                        @Override
                        public void OnFinish() {
                            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        recorder.stop();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                show_outcome();
                                            }
                                        });
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            try {
                                recorder.stop();
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //开始播放
                    player.play();
                    //启动写入线程
                    service.execute(writerTask);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        service.execute(runnable);

        */
    }

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

    //写入播放器线程
    public class WriterTask implements Runnable {

        @Override
        public void run() {
            MyApplication application = (MyApplication) getApplication();
            short[] data = application.sampleSignal;
            int offset = 0, temp;
            while ((temp = player.write(data, offset, data.length - offset)) > 0) {
                offset += temp;
                if (offset == data.length) {
                    break;
                }
            }
            Log.d("WriterTask", "run: ");
        }
    }
}

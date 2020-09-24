package edu.scut.acoustics.ui.experiment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Objects;
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

    public void show_outcome() {
        Fragment fragment = new OutcomeFragment();
        getSupportFragmentManager().beginTransaction().replace(binding.frame.getId(), fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onClick(final View view) {
        //取消点击响应
        view.setEnabled(false);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //启动录音
                    recorder.start();
                    //设置播放结束位置
                    MyApplication application = (MyApplication) getApplication();
                    player.setMarker(application.sampleSignal.length);
                    //设置播放结束接口
                    player.setOnFinishListener(new SampleMusicPlayer.OnFinishListener() {
                        @Override
                        public void OnFinish() {
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
        }
    }
}

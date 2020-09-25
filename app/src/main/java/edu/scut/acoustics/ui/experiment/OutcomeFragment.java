package edu.scut.acoustics.ui.experiment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FramentOutcomeBinding;
import edu.scut.acoustics.utils.DSPMath;

import com.github.mikephil.charting.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OutcomeFragment extends Fragment {
    private String filename;
    private FramentOutcomeBinding binding;
    private ExecutorService service = Executors.newCachedThreadPool();
    private Handler handler = new Handler(Looper.getMainLooper());
    private float[] recordData;
    private float[] convolutionData;
    private float[] inverseData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frament_outcome, container, false);
        ExperimentActivity activity = (ExperimentActivity)requireActivity();
        MyApplication application = (MyApplication) requireActivity().getApplication();
        inverseData = application.inverseSignal;
        filename = activity.filename;
        return binding.getRoot();
    }

    public void draw_chart(){

    }

    public class DataProcess implements Runnable{

        @Override
        public void run() {
            try {
                File file = new File(filename);
                if (file.exists()) {
                    //打开文件
                    FileInputStream fis = new FileInputStream(file);
                    //获得音频长度
                    long length = (fis.getChannel().size() - 44) / 2;
                    //创建数组
                    recordData = new float[(int) length];
                    convolutionData = new float[recordData.length + inverseData.length - 1];
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    //跳过文件头
                    for (int i = 0; i < 44; i++) {
                        bis.read();
                    }
                    //读入音频数据，并转为float类型
                    int temp;
                    final int SHORT_MAX = (int) Short.MAX_VALUE + 1;
                    for (int i = 0; i < recordData.length; i++) {
                        temp = bis.read();
                        temp |= bis.read() << 8;
                        recordData[i] = (float) temp / SHORT_MAX;
                    }
                    DSPMath dspMath = new DSPMath();
                    dspMath.conv(recordData, inverseData, convolutionData);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.shutdownNow();
    }
}

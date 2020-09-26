package edu.scut.acoustics.ui.experiment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FramentOutcomeBinding;
import edu.scut.acoustics.utils.DSPMath;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OutcomeFragment extends Fragment {
    private String filename;
    private FramentOutcomeBinding binding;
    private ExecutorService service = Executors.newCachedThreadPool();
    private Handler handler = new Handler(Looper.getMainLooper());
    private float[] recordData;
    private float[] convolutionData;
    private float[] tailoredData;
    private float[] inverseData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frament_outcome, container, false);
        ExperimentActivity activity = (ExperimentActivity)requireActivity();
        MyApplication application = (MyApplication) requireActivity().getApplication();
        inverseData = application.inverseSignal;
        filename = activity.filename;
        DataProcess process = new DataProcess();
        service.submit(process);
        return binding.getRoot();
    }

    public void drawChart(LineChart chart, float[] y, Float max, Float min, String label){
        final int MAXPOINT = 3000;
        int dpp = y.length / MAXPOINT;
        if(dpp == 0){
            dpp = 1;
        }
        //图表初始化
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);
        //数据初始化
        List<Entry> values = new ArrayList<>(y.length / dpp + 1);
        //如果没有指定最大值和最小值时，将自动寻找
        if(max == null || min == null){
            max = 0f; min = 0f;
            float low, high;
            for (int i = 0; i < y.length; i += dpp) {
                low = y[i];
                high = y[i];
                for (int j = i, k = 0; j < y.length && k < dpp; ++j, ++k) {
                    if(y[j] > max){
                        max = y[j];
                    }
                    if(y[j] < min){
                        min = y[j];
                    }
                    if(y[j] < low){
                        low = y[j];
                    }
                    if(y[j] > high){
                        high = y[j];
                    }
                }
                values.add(new Entry(i,low));
                values.add(new Entry(i + (float)dpp / 2,high));
            }
        }
        else {
            float low, high;
            for (int i = 0; i < y.length; i += dpp) {
                low = y[i];
                high = y[i];
                for (int j = i, k = 0; j < y.length && k < dpp; ++j, ++k) {
                    if(y[j] < low){
                        low = y[j];
                    }
                    if(y[j] > high){
                        high = y[j];
                    }
                }
                values.add(new Entry(i,low));
                values.add(new Entry(i + (float)dpp / 2,high));
            }
        }
        //坐标轴初始化
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(max);
        yAxis.setAxisMinimum(min);
        //创建图表数据集
        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            //已有数据集，通知重画
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        }
        else {
            set1 = new LineDataSet(values, label);
            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setLineWidth(0.1f);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setDrawFilled(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);

            chart.setData(data);
        }
        chart.animateX(1);
        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
        Log.d("data load", "drawChart: ");
    }

    public void convolution_tailor(){
        if(convolutionData != null){
            int index = 0, lmost, rmost;
            float max = 0, temp;
            for (int i = 0; i < convolutionData.length; i++) {
                temp = Math.abs(convolutionData[i]);
                if(temp > max){
                    max = temp;
                    index = i;
                }
            }
            lmost = (int) (index - 44100f * 0.01f);
            rmost = (int) (index + 44100f * 0.05f);
            if(lmost < 0){
                lmost = 0;
            }
            if(rmost >= convolutionData.length){
                rmost = convolutionData.length - 1;
            }
            tailoredData = new float[rmost - lmost + 1];
            System.arraycopy(convolutionData, lmost, tailoredData, 0, rmost + 1 - lmost);
        }
    }

    public void drawChart(LineChart chart, float[] x, float[] y, String label){

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
                    short temp;
                    final int SHORT_MAX = (int) Short.MAX_VALUE + 1;
                    for (int i = 0; i < recordData.length; i++) {
                        temp = (short) bis.read();
                        temp |= bis.read() << 8;
                        recordData[i] = (float) temp / SHORT_MAX;
                    }
                    //生产卷积信号
                    DSPMath dspMath = new DSPMath();
                    Log.d("process data", "run: ");
                    dspMath.conv(recordData, inverseData, convolutionData);
                    convolution_tailor();
                    if(handler != null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("wave chart", "run: draw");
                                drawChart(binding.convolutionWave,tailoredData,null,null,
                                        getResources().getString(R.string.convolution_wave));
                            }
                        });
                    }


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

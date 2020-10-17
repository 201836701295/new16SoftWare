package edu.scut.acoustics.ui.experiment;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import edu.scut.acoustics.R;
import edu.scut.acoustics.utils.DSPMath;

public class ChartRepository {
    //图表最大数据数
    public static final int MAX_ENTRY = 3000;
    private final DSPMath dspMath = new DSPMath();
    private final String waveLabel;
    private final String phaseLabel;
    private final String powerLabel;
    MutableLiveData<ChartInformation> waveChartLiveData;
    MutableLiveData<ChartInformation> phaseChartLiveData;
    MutableLiveData<ChartInformation> powerChartLiveData;
    private float[] convolutionData;
    private float[] tailorData;
    private float[] real;
    private float[] imagine;
    private float[] phase;
    private float[] power;
    private float[] frequency;
    private ChartInformation waveChart;
    private ChartInformation phaseChart;
    private ChartInformation powerChart;
    private float[] audioData1;
    private float[] audioData2;

    public ChartRepository(Context context) {
        waveLabel = context.getResources().getString(R.string.convolution_wave);
        phaseLabel = context.getResources().getString(R.string.phase_chart);
        powerLabel = context.getResources().getString(R.string.power_chart);
        waveChartLiveData = new MutableLiveData<>();
        phaseChartLiveData = new MutableLiveData<>();
        powerChartLiveData = new MutableLiveData<>();
    }

    public ChartRepository(Context context, float[] a1, float[] a2) {
        waveLabel = context.getResources().getString(R.string.convolution_wave);
        phaseLabel = context.getResources().getString(R.string.phase_chart);
        powerLabel = context.getResources().getString(R.string.power_chart);
        audioData1 = a1;
        audioData2 = a2;
    }

    public LiveData<ChartInformation> getPhaseChartLiveData() {
        return phaseChartLiveData;
    }

    public LiveData<ChartInformation> getWaveChartLiveData() {
        return waveChartLiveData;
    }

    public LiveData<ChartInformation> getPowerChartLiveData() {
        return powerChartLiveData;
    }

    public void setAudioData1(float[] audioData1) {
        this.audioData1 = audioData1;
    }

    public void setAudioData2(float[] audioData2) {
        this.audioData2 = audioData2;
    }

    /**
     * 子线程处理数据
     *
     * @throws Exception
     */
    public void doFinal() throws Exception {
        if (audioData1 == null || audioData2 == null) {
            throw new Exception("audio data not initialized");
        }
        //作卷积
        convolutionData = new float[audioData1.length + audioData2.length - 1];
        dspMath.conv(audioData1, audioData2, convolutionData);
        //作裁剪
        tailor();
        //作fft
        real = new float[tailorData.length];
        imagine = new float[tailorData.length];
        dspMath.fft(tailorData, tailorData.length, real, imagine);
        //作求相位
        phase = new float[(tailorData.length + 1) / 2];
        dspMath.phase(real, imagine, phase);
        //求功率
        power = new float[(tailorData.length + 1) / 2];
        frequency = new float[(tailorData.length + 1) / 2];
        dspMath.welch(tailorData, tailorData.length, 44100, power, frequency);


        //生成图像数据
        produce_chart();
    }

    //裁剪数据
    private void tailor() {
        //index 峰值坐标 lmost裁剪数据左端 rmost 裁剪数据右端
        int index = 0, lmost, rmost;
        float max = 0, temp;
        for (int i = 0; i < convolutionData.length; i++) {
            temp = Math.abs(convolutionData[i]);
            if (temp > max) {
                max = temp;
                index = i;
            }
        }
        //截取峰值前0.1s和峰值后0.05s
        lmost = (int) (index - 44100 * 0.01f);
        rmost = (int) (index + 44100 * 0.05f);
        //检查是否超越边界
        if (lmost < 0) {
            lmost = 0;
        }
        if (rmost >= convolutionData.length) {
            rmost = convolutionData.length - 1;
        }
        //复制数据
        tailorData = new float[rmost - lmost + 1];
        System.arraycopy(convolutionData, lmost, tailorData, 0, rmost + 1 - lmost);
    }

    //生成图表
    private void produce_chart() {
        wave_chart();
        power_chart();
        phase_chart();
    }

    //生成波形图表
    private void wave_chart() {
        waveChart = new ChartInformation();
        //设置坐标轴标签
        waveChart.labelX = "时间/s";
        waveChart.labelY = "振幅";
        waveChart.xUnit = "s";
        waveChart.yUnit = "";
        //设置坐标轴显示范围
        waveChart.maxX = tailorData.length / 44100f;
        waveChart.minX = 0;
        waveChart.maxY = 0;
        waveChart.minY = 0;
        //设置采样区间
        int dpp = tailorData.length / MAX_ENTRY;
        if (dpp == 0) {
            dpp = 1;
        }
        //数据点数据
        List<Entry> values = new ArrayList<>(tailorData.length / dpp + 1);
        //区间最值
        float low, high;
        for (int i = 0; i < tailorData.length; i += dpp) {
            low = tailorData[i];
            high = tailorData[i];
            //更新Y轴范围，取区间最值
            for (int j = i, k = 0; j < tailorData.length && k < dpp; ++j, ++k) {
                if (tailorData[j] > waveChart.maxY) {
                    waveChart.maxY = tailorData[j];
                }
                if (tailorData[j] < waveChart.minY) {
                    waveChart.minY = tailorData[j];
                }
                if (tailorData[j] < low) {
                    low = tailorData[j];
                }
                if (tailorData[j] > high) {
                    high = tailorData[j];
                }
            }
            //将最值采样
            values.add(new Entry(i / 44100f, low));
            values.add(new Entry(i / 44100f, high));
        }
        //设置图线绘制方法
        LineDataSet set = new LineDataSet(values, waveLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        set.setFormLineWidth(1f);
        set.setFormSize(15.f);
        set.setValueTextSize(9f);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        waveChart.lineData = new LineData(dataSets);
        waveChartLiveData.postValue(waveChart);
    }

    private void power_chart() {
        powerChart = new ChartInformation();
        powerChart.labelX = "频率/Hz";
        powerChart.labelY = "功率/dB";
        powerChart.xUnit = "Hz";
        powerChart.yUnit = "dB";
        powerChart.minX = (float) Math.log10(frequency[1]);
        powerChart.maxX = (float) Math.log10(frequency[frequency.length - 1]);
        Log.i("power chart", "powerChart.minX: " + powerChart.minX);
        Log.i("power chart", "powerChart.maxX: " + powerChart.maxX);
        powerChart.maxY = 0;
        powerChart.minY = 0;

        int dpp = power.length / MAX_ENTRY;
        if (dpp == 0) {
            dpp = 1;
        }
        List<Entry> values = new ArrayList<>(power.length / dpp + 1);
        float low, high;
        for (int i = 1; i < power.length; i += dpp) {
            low = power[i];
            high = power[i];
            for (int j = i, k = 0; j < power.length && k < dpp; ++j, ++k) {
                if (power[j] > powerChart.maxY) {
                    powerChart.maxY = power[j];
                }
                if (power[j] < powerChart.minY) {
                    powerChart.minY = power[j];
                }
                if (power[j] < low) {
                    low = power[j];
                }
                if (power[j] > high) {
                    high = power[j];
                }
            }
            values.add(new Entry((float) Math.log10(frequency[i]), low));
            values.add(new Entry((float) Math.log10(frequency[i]), high));
            Log.i("power chart", "powerChart: " + (float) Math.log10(frequency[i]));
        }
        LineDataSet set = new LineDataSet(values, powerLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        set.setFormLineWidth(1f);
        set.setFormSize(15.f);
        set.setValueTextSize(9f);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        powerChart.lineData = new LineData(dataSets);
        powerChartLiveData.postValue(powerChart);
    }

    private void phase_chart() {
        phaseChart = new ChartInformation();
        phaseChart.labelX = "频率/Hz";
        phaseChart.labelY = "相位";
        phaseChart.xUnit = "Hz";
        phaseChart.yUnit = "";
        phaseChart.minX = 0;
        phaseChart.maxX = frequency[frequency.length - 1];
        phaseChart.maxY = (float) (Math.PI / 2);
        phaseChart.minY = -(float) (Math.PI / 2);

        int dpp = phase.length / MAX_ENTRY;
        if (dpp == 0) {
            dpp = 1;
        }
        List<Entry> values = new ArrayList<>(phase.length / dpp + 1);
        float low, high;
        for (int i = 0; i < phase.length; i += dpp) {
            low = phase[i];
            high = phase[i];
            for (int j = i, k = 0; j < phase.length && k < dpp; ++j, ++k) {
                if (phase[j] < low) {
                    low = phase[j];
                }
                if (phase[j] > high) {
                    high = phase[j];
                }
            }
            values.add(new Entry(frequency[i], low));
            values.add(new Entry(frequency[i], high));
        }
        LineDataSet set = new LineDataSet(values, phaseLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        set.setFormLineWidth(1f);
        set.setFormSize(15.f);
        set.setValueTextSize(9f);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        phaseChart.lineData = new LineData(dataSets);
        phaseChartLiveData.postValue(phaseChart);
    }
}

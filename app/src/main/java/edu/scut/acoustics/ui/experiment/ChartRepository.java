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
    final DSPMath dspMath = new DSPMath();
    final String waveLabel;
    final String phaseLabel;
    final String powerLabel;
    final String audioLabel;
    MutableLiveData<ChartInformation> waveChartLiveData;
    MutableLiveData<ChartInformation> phaseChartLiveData;
    MutableLiveData<ChartInformation> powerChartLiveData;
    MutableLiveData<ChartInformation> audioChartLiveData;
    float[] convolutionData;
    float[] tailorData;
    float[] real;
    float[] imagine;
    float[] phase;
    float[] power;
    float[] frequency;
    ChartInformation waveChart;
    ChartInformation phaseChart;
    ChartInformation powerChart;
    ChartInformation audioChart;
    float[] audioData1;
    float[] audioData2;

    public ChartRepository(Context context) {
        waveLabel = context.getResources().getString(R.string.convolution_wave);
        phaseLabel = context.getResources().getString(R.string.phase_chart);
        powerLabel = context.getResources().getString(R.string.power_chart);
        audioLabel = "接受波形";
        waveChartLiveData = new MutableLiveData<>();
        phaseChartLiveData = new MutableLiveData<>();
        powerChartLiveData = new MutableLiveData<>();
        audioChartLiveData = new MutableLiveData<>();
    }

    public ChartRepository(Context context, float[] a1, float[] a2) {
        waveLabel = context.getResources().getString(R.string.convolution_wave);
        phaseLabel = context.getResources().getString(R.string.phase_chart);
        powerLabel = context.getResources().getString(R.string.power_chart);
        audioLabel = "接受波形";
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

    public LiveData<ChartInformation> getAudioChartLiveData() {
        return audioChartLiveData;
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
        //dspMath.phase(real, imagine, phase);
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
        rmost = (int) (index + 44100 * 0.05f) - 1;
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
        audio_chart();
    }

    void audio_chart() {
        ChartInformation chartInformation = audioChart = new ChartInformation();
        //设置坐标轴标签
        chartInformation.labelX = "时间/s";
        chartInformation.labelY = "振幅";
        chartInformation.xUnit = "s";
        chartInformation.yUnit = "";
        //设置坐标轴显示范围
        chartInformation.maxX = audioData2.length / 44100f;
        chartInformation.minX = 0;
        chartInformation.maxY = Short.MAX_VALUE * 1.1f;
        chartInformation.minY = -Short.MAX_VALUE * 1.1f;
        int dpp = audioData2.length / MAX_ENTRY;
        if (dpp == 0) {
            dpp = 1;
        }
        //数据点数据
        List<Entry> values = new ArrayList<>(audioData2.length / dpp + 1);
        float low, high;
        for (int i = 0; i < audioData2.length; i += dpp) {
            low = audioData2[i];
            high = audioData2[i];
            for (int j = i, k = 0; j < audioData2.length && k < dpp; ++j, ++k) {
                if (audioData2[j] < low) {
                    low = audioData2[j];
                }
                if (audioData2[j] > high) {
                    high = audioData2[j];
                }
            }
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
        chartInformation.lineData = new LineData(dataSets);
        audioChartLiveData.postValue(audioChart);
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
        //数据点数据
        List<Entry> values = new ArrayList<>(tailorData.length);
        for (int i = 0; i < tailorData.length; ++i) {
            //更新Y轴范围，取区间最值
            if (tailorData[i] > waveChart.maxY) {
                waveChart.maxY = tailorData[i];
            }
            if (tailorData[i] < waveChart.minY) {
                waveChart.minY = tailorData[i];
            }
            values.add(new Entry(i / 44100f, tailorData[i]));
        }
        waveChart.maxY *= 1.1f;
        waveChart.minY *= 1.1f;
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
        final float constant = (float) Math.log10(32768 * 32768) * 10;
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

        List<Entry> values = new ArrayList<>(power.length);
        for (int i = 1; i < power.length; ++i) {
            if (power[i] > powerChart.maxY) {
                powerChart.maxY = power[i];
            }
            if (power[i] < powerChart.minY) {
                powerChart.minY = power[i];
            }
            values.add(new Entry((float) Math.log10(frequency[i]), power[i] + constant));
            Log.i("power chart", "powerChart: " + (float) Math.log10(frequency[i]));
        }
        float temp = (powerChart.maxY - powerChart.minY) * 0.2f;
        powerChart.maxY += temp;
        powerChart.minY -= temp;
        powerChart.maxY += constant;
        powerChart.minY += constant;

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

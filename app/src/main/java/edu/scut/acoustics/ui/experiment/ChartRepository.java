package edu.scut.acoustics.ui.experiment;

import android.content.Context;
import android.graphics.Color;

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
    private DSPMath dspMath = new DSPMath();
    private final float[] audioData1;
    private final float[] audioData2;
    private float[] convolutionData;
    private float[] tailorData;
    private float[] real;
    private float[] imagine;
    private float[] length;
    private float[] phase;
    private float[] power;
    private float[] frequency;
    private String waveLabel;
    private String frequencyLabel;
    private String phaseLabel;
    private String powerLabel;
    private ChartInformation waveChart;
    private ChartInformation frequencyChart;
    private ChartInformation phaseChart;
    private ChartInformation powerChart;

    public ChartRepository(Context context, float[] a1, float[] a2){
        waveLabel = context.getResources().getString(R.string.convolution_wave);
        frequencyLabel = context.getResources().getString(R.string.frequency_chart);
        phaseLabel = context.getResources().getString(R.string.phase_chart);
        powerLabel = context.getResources().getString(R.string.power_chart);
        audioData1 = a1;
        audioData2 = a2;
    }

    public void doFinal() throws Exception {
        if(audioData1 == null || audioData2 == null){
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
        dspMath.fft(tailorData,tailorData.length,real,imagine);
        //作求模和相位
        phase = new float[tailorData.length];
        length = new float[tailorData.length];
        dspMath.phaseAndLength(real,imagine,phase,length);
        //求功率
        power = new float[tailorData.length];
        frequency = new float[tailorData.length];
        dspMath.welch(tailorData,tailorData.length,44100,power,frequency);
        //生成图像数据
        produce_chart();
    }

    //裁剪数据
    private void tailor(){
        //index 峰值坐标 lmost裁剪数据左端 rmost 裁剪数据右端
        int index = 0, lmost, rmost;
        float max = 0, temp;
        for (int i = 0; i < convolutionData.length; i++) {
            temp = Math.abs(convolutionData[i]);
            if(temp > max){
                max = temp;
                index = i;
            }
        }
        //截取峰值前0.1s和峰值后0.05s
        lmost = (int) (index - 44100 * 0.01f);
        rmost = (int) (index + 44100 * 0.05f);
        //检查是否超越边界
        if(lmost < 0){
            lmost = 0;
        }
        if(rmost >= convolutionData.length){
            rmost = convolutionData.length - 1;
        }
        //复制数据
        tailorData = new float[rmost - lmost + 1];
        System.arraycopy(convolutionData, lmost, tailorData, 0, rmost + 1 - lmost);
    }

    //生成图表
    private void produce_chart(){
        wave_chart();
        frequency_chart();
        power_chart();
        phase_chart();
    }

    //生成波形图表
    private void wave_chart(){
        waveChart = new ChartInformation();
        //设置坐标轴标签
        waveChart.labelX = "时间/s";
        waveChart.labelY = "振幅";
        //设置坐标轴显示范围
        waveChart.maxX = tailorData.length / 44100f;
        waveChart.minX = 0;
        waveChart.maxY = 0;
        waveChart.minY = 0;
        //设置采样区间
        int dpp = tailorData.length / MAX_ENTRY;
        if(dpp == 0){
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
                if(tailorData[j] > waveChart.maxY){
                    waveChart.maxY = tailorData[j];
                }
                if(tailorData[j] < waveChart.minY){
                    waveChart.minY = tailorData[j];
                }
                if(tailorData[j] < low){
                    low = tailorData[j];
                }
                if(tailorData[j] > high){
                    high = tailorData[j];
                }
            }
            //将最值采样
            values.add(new Entry(i / 44100f,low));
            values.add(new Entry(i / 44100f,high));
        }
        //设置图线绘制方法
        LineDataSet set = new LineDataSet(values,waveLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(0.1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        waveChart.lineData = new LineData(dataSets);
    }

    private void frequency_chart(){
        frequencyChart = new ChartInformation();
        frequencyChart.labelX = "";
        frequencyChart.labelY = "振幅";
        frequencyChart.maxX = length.length;
        frequencyChart.minX = 0;
        frequencyChart.maxY = 0;
        frequencyChart.minY = 0;

        int dpp = length.length / MAX_ENTRY;
        if(dpp == 0){
            dpp = 1;
        }
        List<Entry> values = new ArrayList<>(length.length / dpp + 1);
        float low, high;
        for (int i = 0; i < length.length; i += dpp) {
            low = length[i];
            high = length[i];
            for (int j = i, k = 0; j < length.length && k < dpp; ++j, ++k) {
                if(length[j] > frequencyChart.maxY){
                    frequencyChart.maxY = length[j];
                }
                if(length[j] < low){
                    low = length[j];
                }
                if(length[j] > high){
                    high = length[j];
                }
            }
            values.add(new Entry(i ,low));
            values.add(new Entry(i ,high));
        }
        LineDataSet set = new LineDataSet(values,frequencyLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(0.1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        frequencyChart.lineData = new LineData(dataSets);
    }

    private void power_chart(){
        powerChart = new ChartInformation();
        powerChart.labelX = "频率/Hz";
        powerChart.labelY = "功率";
        powerChart.minX = 0;
        powerChart.maxX = frequency[frequency.length - 1];
        powerChart.maxY = 0;
        powerChart.minY = 0;

        int dpp = power.length / MAX_ENTRY;
        if(dpp == 0){
            dpp = 1;
        }
        List<Entry> values = new ArrayList<>(power.length / dpp + 1);
        float low, high;
        for (int i = 0; i < power.length; i += dpp) {
            low = power[i];
            high = power[i];
            for (int j = i, k = 0; j < power.length && k < dpp; ++j, ++k) {
                if(power[j] > powerChart.maxY){
                    powerChart.maxY = power[j];
                }
                if(power[j] < low){
                    low = power[j];
                }
                if(power[j] > high){
                    high = power[j];
                }
            }
            values.add(new Entry(i ,low));
            values.add(new Entry(i ,high));
        }
        LineDataSet set = new LineDataSet(values,powerLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(0.1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        powerChart.lineData = new LineData(dataSets);
    }

    private void phase_chart(){
        phaseChart = new ChartInformation();
        phaseChart.labelX = "";
        phaseChart.labelY = "相位";
        phaseChart.minX = 0;
        phaseChart.maxX = phase.length;
        phaseChart.maxY = (float) (Math.PI * 2);
        phaseChart.minY = 0;

        int dpp = phase.length / MAX_ENTRY;
        if(dpp == 0){
            dpp = 1;
        }
        List<Entry> values = new ArrayList<>(phase.length / dpp + 1);
        float low, high;
        for (int i = 0; i < phase.length; i += dpp) {
            low = phase[i];
            high = phase[i];
            for (int j = i, k = 0; j < phase.length && k < dpp; ++j, ++k) {
                if(phase[j] < low){
                    low = phase[j];
                }
                if(phase[j] > high){
                    high = phase[j];
                }
            }
            values.add(new Entry(i ,low));
            values.add(new Entry(i ,high));
        }
        LineDataSet set = new LineDataSet(values,phaseLabel);
        set.setDrawIcons(false);
        set.setColor(Color.BLACK);
        set.setLineWidth(0.1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawFilled(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        phaseChart.lineData = new LineData(dataSets);
    }

    public ChartInformation getWaveChart() {
        return waveChart;
    }

    public ChartInformation getFrequencyChart() {
        return frequencyChart;
    }

    public ChartInformation getPhaseChart() {
        return phaseChart;
    }

    public ChartInformation getPowerChart() {
        return powerChart;
    }
}

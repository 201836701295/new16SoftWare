package edu.scut.acoustics.ui.ear_test;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Vector;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentTestResultBinding;
import edu.scut.acoustics.view.MyMarkerView;

public class TestResultFragment extends Fragment {
    final static int LEFT = 0;
    final static int RIGHT = 1;
    FragmentTestResultBinding binding;
    ResultViewModel viewModel;
    ValueFormatter xValueFormatter, yValueFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_result, container, false);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        xValueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return viewModel.getFrequencies()[index] + "Hz";
            }
        };
        yValueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "dB";
            }
        };
        initialLineChar();
        viewModel.getLeftSensitivitiesLiveData().observe(getViewLifecycleOwner(), new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                setData(ints, LEFT);
            }
        });
        viewModel.getRightSensitivitiesLiveData().observe(getViewLifecycleOwner(), new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                setData(ints, RIGHT);
            }
        });

        return binding.getRoot();
    }

    void initialLineChar() {
        LineChart chart = binding.earChart;
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);

        MyMarkerView myMarkerView = new MyMarkerView(requireContext());
        myMarkerView.setChartView(chart);
        chart.setMarker(myMarkerView);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        chart.getAxisRight().setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(90f);
        yAxis.setAxisMinimum(-20f);
        yAxis.setValueFormatter(yValueFormatter);

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(viewModel.getFrequencies().length - 1);
        xAxis.setAxisMinimum(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(xValueFormatter);
        xAxis.setLabelCount(viewModel.getFrequencies().length, true);
        chart.animateX(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        Vector<Entry> vector = new Vector<>();
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>(3);

        LineDataSet set = new LineDataSet(vector, "左耳");
        set.setDrawIcons(false);
        set.enableDashedLine(10f, 5f, 0f);
        set.setColor(Color.RED);
        set.setCircleColor(Color.RED);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setFormLineWidth(1f);
        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(15.f);
        set.setValueTextSize(9f);
        set.enableDashedHighlightLine(10f, 5f, 0f);

        lineDataSets.add(set);

        set = new LineDataSet(vector, "右耳");
        set.setDrawIcons(false);
        set.enableDashedLine(10f, 5f, 0f);
        set.setColor(Color.BLUE);
        set.setCircleColor(Color.BLUE);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setFormLineWidth(1f);
        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(15.f);
        set.setValueTextSize(9f);
        set.enableDashedHighlightLine(10f, 5f, 0f);

        lineDataSets.add(set);

        LineData lineData = new LineData(lineDataSets);
        chart.setData(lineData);
    }

    void setData(int[] data, int flags) {
        final LineChart chart = binding.earChart;
        Vector<Entry> vector = new Vector<>();
        for (int i = 0; i < data.length; ++i) {
            vector.add(new Entry(i, data[i]));
        }
        LineDataSet set;
        set = (LineDataSet) chart.getData().getDataSetByIndex(flags);
        set.setValues(vector);
        set.notifyDataSetChanged();
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
    }
}

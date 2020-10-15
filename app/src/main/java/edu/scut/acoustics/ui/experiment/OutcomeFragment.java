package edu.scut.acoustics.ui.experiment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.formatter.ValueFormatter;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentOutcomeBinding;
import edu.scut.acoustics.view.MyMarkerView;

public class OutcomeFragment extends Fragment {
    private FragmentOutcomeBinding binding;
    private ExperimentViewModel viewModel;

    void observeChart(LineChart chart, final ChartInformation chartInformation) {
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(chartInformation.maxY);
        yAxis.setAxisMinimum(chartInformation.minY);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + chartInformation.yUnit;
            }
        });

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(chartInformation.maxX);
        xAxis.setAxisMinimum(chartInformation.minX);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + chartInformation.xUnit;
            }
        });
        chart.setData(chartInformation.lineData);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //UI初始化
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_outcome, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ExperimentViewModel.class);
        initialLineChar();
        viewModel.getWaveChart().observe(getViewLifecycleOwner(), new Observer<ChartInformation>() {
            @Override
            public void onChanged(ChartInformation chartInformation) {
                Log.d("observe", "onChanged: ");
                if (chartInformation != null) {
                    observeChart(binding.waveChart, chartInformation);
                    binding.waveChart.notifyDataSetChanged();
                }
            }
        });
        viewModel.getPhaseChart().observe(getViewLifecycleOwner(), new Observer<ChartInformation>() {
            @Override
            public void onChanged(ChartInformation chartInformation) {
                if (chartInformation != null) {
                    observeChart(binding.phaseChart, chartInformation);
                    binding.phaseChart.notifyDataSetChanged();
                }
            }
        });
        viewModel.getPowerChart().observe(getViewLifecycleOwner(), new Observer<ChartInformation>() {
            @Override
            public void onChanged(ChartInformation chartInformation) {
                if (chartInformation != null) {
                    observeChart(binding.powerChart, chartInformation);
                    binding.powerChart.notifyDataSetChanged();
                }
            }
        });
        return binding.getRoot();
    }

    void initialLineChar() {
        LineChart[] charts = {binding.phaseChart, binding.powerChart, binding.waveChart};
        for (LineChart chart : charts) {
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
            chart.animateX(10);
            Legend l = chart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
        }
    }

}

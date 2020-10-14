package edu.scut.acoustics.ui.experiment;

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

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.FragmentOutcomeBinding;

public class OutcomeFragment extends Fragment {
    private FragmentOutcomeBinding binding;
    private ChartViewModel viewModel;

    private void observeChart(LineChart chart, ChartInformation chartInformation) {
        //图表初始化
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);
        //坐标轴初始化
        Log.d("draw chart", "observeChart: ");
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(chartInformation.maxY);
        yAxis.setAxisMinimum(chartInformation.minY);
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(chartInformation.maxX);
        xAxis.setAxisMinimum(chartInformation.minX);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setData(chartInformation.lineData);
        chart.animateX(1);
        Legend l = chart.getLegend();
        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //UI初始化
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_outcome, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ChartViewModel.class);
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
                }
            }
        });
        viewModel.getFrequencyChart().observe(getViewLifecycleOwner(), new Observer<ChartInformation>() {
            @Override
            public void onChanged(ChartInformation chartInformation) {
                if (chartInformation != null) {
                    observeChart(binding.frequencyChart, chartInformation);
                }
            }
        });
        viewModel.getPowerChart().observe(getViewLifecycleOwner(), new Observer<ChartInformation>() {
            @Override
            public void onChanged(ChartInformation chartInformation) {
                if (chartInformation != null) {
                    observeChart(binding.powerChart, chartInformation);
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getFrequencyChart().removeObservers(getViewLifecycleOwner());
        viewModel.getPhaseChart().removeObservers(getViewLifecycleOwner());
        viewModel.getPowerChart().removeObservers(getViewLifecycleOwner());
        viewModel.getWaveChart().removeObservers(getViewLifecycleOwner());
    }
}

package edu.scut.acoustics.ui.noise_measurement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityNoiseMeasurementBinding;
import edu.scut.acoustics.ui.adjust.AdjustActivity;
import edu.scut.acoustics.utils.SLM;

public class NoiseMeasurementActivity extends AppCompatActivity implements View.OnClickListener {
    final static int PERMISSIONS_FOR_DBA = 1;

    ActivityNoiseMeasurementBinding binding;
    NoiseViewModel viewModel;
    DecimalFormat format = new DecimalFormat("###0.00");
    float baseline;
    MyApplication application;
    ValueFormatter xValueFormatter;
    ArrayList<BarEntry> barEntries;
    int foo = 0;
    int max = 150;
    Timer timer = new Timer();
    TimerTask timerTask;
    SLM.DBA dba = new SLM.DBA();
    MutableLiveData<SLM.DBA> dbaMutableLiveData = new MutableLiveData<>(dba);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_noise_measurement);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        barEntries = new ArrayList<>(8);
        initialBarChart();
        viewModel = new ViewModelProvider(this).get(NoiseViewModel.class);
        viewModel.getMax().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.max.setText(getString(R.string.max_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getMin().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.min.setText(getString(R.string.min_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getRealtime().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.real.setText(getString(R.string.real_time_db, format.format(aFloat + baseline)));
            }
        });
        viewModel.getSourceType().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.sourceType.setText(s);
            }
        });
        viewModel.getDba().observe(this, new Observer<SLM.DBA>() {
            @Override
            public void onChanged(SLM.DBA dba) {
                observeDBA(dba);
            }
        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < dba.yValue.length; i++) {
                    dba.yValue[i] = max;
                    dba.xAxisValue[i] = 10 * i;
                }
                max -= 1;
                dbaMutableLiveData.postValue(dba);
            }
        };
        //timer.schedule(timerTask, 0, 1000);
        dbaMutableLiveData.observe(this, new Observer<SLM.DBA>() {
            @Override
            public void onChanged(SLM.DBA dba) {
                observeDBA(dba);
            }
        });

        binding.refresh.setOnClickListener(this);
        binding.adjust.setOnClickListener(this);
        application = (MyApplication) getApplication();
    }

    void initialBarChart() {
        BarChart chart = binding.dbChart;
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(8);
        chart.setScaleEnabled(false);
        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(8);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(7.5f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(150f);
        yAxis.setAxisMinimum(-30f);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "dBA";
            }
        });


        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);
    }

    void observeDBA(final SLM.DBA dba) {
        BarChart chart = binding.dbChart;
        barEntries.clear();

        if (xValueFormatter == null) {
            xValueFormatter = new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int index = (int) value;
                    if (index < 0) {
                        return "";
                    }
                    return (int) dba.xAxisValue[index] + "Hz";
                }
            };
            chart.getXAxis().setValueFormatter(xValueFormatter);
        }
        else {
            chart.getXAxis().setValueFormatter(xValueFormatter);
        }
        for (int i = 0; i < dba.yValue.length; i++) {
            barEntries.add(new BarEntry(i, dba.yValue[i] + baseline));
            //Log.i("barEntries", "BarEntry: " + i + " " + dba.yValue[i]);
        }
        BarDataSet set;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(barEntries);
            set.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(barEntries, "倍频声压级");
            set.setDrawIcons(false);
            set.setColors(Color.RED);
            BarData barData = new BarData(set);
            chart.setData(barData);
        }
        chart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        baseline = getSharedPreferences(getString(R.string.sharedpreferences), MODE_PRIVATE)
                .getFloat(getString(R.string.baseline), 0.0f);
        viewModel.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.adjust.getId()) {
            start_adjust();
        }
        if (view.getId() == binding.refresh.getId()) {
            refresh();
        }
    }

    void refresh() {
        viewModel.refresh();
    }

    public void start_adjust() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_DBA);
        } else {
            viewModel.stop();
            startActivity(new Intent(this, AdjustActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_FOR_DBA) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            start_adjust();
        }
    }
}

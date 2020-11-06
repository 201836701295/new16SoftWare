package edu.scut.acoustics.ui.noise;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import edu.scut.acoustics.MyApplication;
import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityNoiseBinding;
import edu.scut.acoustics.ui.adjust.AdjustActivity;
import edu.scut.acoustics.utils.SLM;

public class NoiseActivity extends AppCompatActivity {
    final static int PERMISSIONS_FOR_SLM = 1;
    final static int PERMISSIONS_FOR_ADJUST = 2;
    ActivityNoiseBinding binding;
    NoiseViewModel viewModel;
    ActionBarDrawerToggle toggle;
    DecimalFormat format = new DecimalFormat("###0.00");
    float baseline;
    MyApplication application;
    ValueFormatter xValueFormatter;
    ArrayList<BarEntry> barEntries;
    TextView[] textViews;
    int unit = R.string.dBA;
    int[] frequencies;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_noise);
        setSupportActionBar(binding.toolbar);
        toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open, R.string.close);
        toggle.syncState();
        //Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        barEntries = new ArrayList<>(8);
        //初始化图表
        initialBarChart();

        frequencies = getResources().getIntArray(R.array.noise_frequency);

        textViews = new TextView[8];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = new TextView(this);
            textViews[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            if (i < 2) {
                binding.linearLayout1.addView(textViews[i], layoutParams);
            } else if (i < 4) {
                binding.linearLayout2.addView(textViews[i], layoutParams);
            } else if (i < 6) {
                binding.linearLayout3.addView(textViews[i], layoutParams);
            } else {
                binding.linearLayout4.addView(textViews[i], layoutParams);
            }
        }

        binding.weighting.setText(R.string.a_weighting);

        viewModel = new ViewModelProvider(this).get(NoiseViewModel.class);
        viewModel.getMax().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.max.setText(getString(R.string.max_db, getString(unit, format.format(aFloat + baseline))));
            }
        });
        viewModel.getMin().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.min.setText(getString(R.string.min_db, getString(unit, format.format(aFloat + baseline))));
            }
        });
        viewModel.getRealtime().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.real.setText(getString(R.string.real_time_db, getString(unit, format.format(aFloat + baseline))));
            }
        });
        viewModel.getSourceType().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.sourceType.setText(s);
            }
        });
        viewModel.getDb().observe(this, new Observer<SLM.DB>() {
            @Override
            public void onChanged(SLM.DB DB) {
                observeDB(DB);
            }
        });
        viewModel.getMaxAmp().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d("audioRecordView", "onChanged: " + integer);
                binding.audioRecordView.update(integer);
            }
        });
        viewModel.isRunning().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    binding.fab.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    binding.fab.setImageResource(R.drawable.ic_baseline_stop_24);
                }
            }
        });


        binding.navView.setCheckedItem(R.id.a_weighting);
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.navView.setCheckedItem(item);
                if (item.getItemId() == R.id.a_weighting) {
                    viewModel.setMode(SLM.A_WEIGHTING);
                    unit = R.string.dBA;
                    binding.weighting.setText(R.string.a_weighting);
                    viewModel.refresh();
                }
                if (item.getItemId() == R.id.c_weighting) {
                    viewModel.setMode(SLM.C_WEIGHTING);
                    unit = R.string.dBC;
                    binding.weighting.setText(R.string.c_weighting);
                    viewModel.refresh();
                }
                if (item.getItemId() == R.id.z_weighting) {
                    viewModel.setMode(SLM.Z_WEIGHTING);
                    unit = R.string.dBZ;
                    binding.weighting.setText(R.string.z_weighting);
                    viewModel.refresh();
                }
                if (item.getItemId() == R.id.adjust) {
                    startAdjust();
                }
                if (item.getItemId() == R.id.quit) {
                    finish();
                }
                binding.drawer.closeDrawers();
                return false;
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("binding.fab", "onClick: " + viewModel.isRecording());
                if (viewModel.isRecording()) {
                    viewModel.stop();
                } else {
                    startSLM();
                }
                Log.d("binding.fab", "onClick: " + viewModel.isRecording());
            }
        });

        application = (MyApplication) getApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            viewModel.refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void startSLM() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_SLM);
        } else {
            binding.audioRecordView.recreate();
            viewModel.start();
            Log.d("viewModel.start", "startSLM: ");
        }
    }

    void startAdjust() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_FOR_ADJUST);
        } else {
            viewModel.stop();
            startActivity(new Intent(this, AdjustActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_FOR_SLM) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            startSLM();
        }
        if (requestCode == PERMISSIONS_FOR_ADJUST) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    application.show_toast(R.string.you_refuse_authorize);
                    return;
                }
            }
            startAdjust();
        }
    }

    void observeDB(final SLM.DB db) {
        BarChart chart = binding.dbChart;
        barEntries.clear();

        if (xValueFormatter == null) {
            xValueFormatter = new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    int index = (int) value;
                    if (index < 0 || index >= 8) {
                        return "";
                    }
                    return frequencies[index] + "Hz";
                }
            };
            chart.getXAxis().setValueFormatter(xValueFormatter);
        }
        for (int i = 0; i < 8; i++) {
            barEntries.add(new BarEntry(i, db.yValue[i] + baseline));
            textViews[i].setText(getString(R.string.frequency_dbUnit, frequencies[i], getString(unit, format.format(db.yValue[i]))));
            //Log.i("barEntries", "BarEntry: " + i + " " + db.yValue[i]);
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
        yAxis.setAxisMaximum(110f);
        yAxis.setAxisMinimum(-10f);
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

    @Override
    protected void onResume() {
        super.onResume();
        baseline = getSharedPreferences(getString(R.string.sharedpreferences), MODE_PRIVATE)
                .getFloat(getString(R.string.baseline), 0.0f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stop();
    }
}

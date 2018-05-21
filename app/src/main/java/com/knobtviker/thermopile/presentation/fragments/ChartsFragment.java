package com.knobtviker.thermopile.presentation.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bojan on 15/06/2017.
 */

public class ChartsFragment extends BaseFragment {
    public static final String TAG = ChartsFragment.class.getSimpleName();

    @BindView(R.id.chart)
    public LineChart lineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupChart();
        setupAxes();
        setupData();
        setLegend();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {

    }

    @OnClick({R.id.button_back})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_back:
                NavHostFragment.findNavController(this).navigate(R.id.action_chartsFragment_to_mainFragment);
                break;
        }
    }

    private void setupChart() {
        // disable description text
        lineChart.getDescription().setEnabled(false);
        // enable touch gestures
        lineChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        // enable scaling
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        // set an alternative background color
        lineChart.setBackgroundColor(Color.DKGRAY);
    }

    private void setupAxes() {
        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(16.0f); //TOTAL_MEMORY
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Add a limit line
        LimitLine ll = new LimitLine(12.0f, "Upper Limit"); //LIMIT_MAX_MEMORY
        ll.setLineWidth(2f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextSize(10f);
        ll.setTextColor(Color.WHITE);
        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        lineChart.setData(data);
    }

    private void setLegend() {
        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.WHITE);
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Memory Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS[0]);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        // To show values of each point
        set.setDrawValues(true);

        return set;
    }

    private void addEntry(Temperature stat) {
        LineData data = lineChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), stat.value), 0);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

            // limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(15);

            // move to the latest entry
            lineChart.moveViewToX(data.getEntryCount());
        }
    }
}

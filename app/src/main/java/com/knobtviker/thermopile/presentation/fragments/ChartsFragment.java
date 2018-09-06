package com.knobtviker.thermopile.presentation.fragments;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.Guideline;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Motion;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.shared.SingleModel;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.views.adapters.ChartAdapter;
import com.knobtviker.thermopile.presentation.views.spark.SparkView;
import com.knobtviker.thermopile.presentation.views.spark.animation.MorphSparkAnimator;

import java.util.List;

import javax.inject.Inject;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.OnItemSelected;

/**
 * Created by bojan on 15/06/2017.
 */

public class ChartsFragment extends BaseFragment<ChartsContract.Presenter> implements ChartsContract.View, SparkView.OnScrubListener {

    @Inject
    ChartAdapter<SingleModel> sparkAdapter;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.spinner_type)
    public Spinner spinnerType;

    @BindView(R.id.spinner_interval)
    public Spinner spinnerInterval;

    @BindView(R.id.guideline_split)
    public Guideline guidelineSplit;

    @BindView(R.id.sparkview)
    public SparkView sparkView;

    @BindView(R.id.textview_max_value)
    public TextView textViewMaxValue;

    @BindView(R.id.textview_min_value)
    public TextView textViewMinValue;

    @BindView(R.id.textview_max_unit)
    public TextView textViewMaxUnit;

    @BindView(R.id.textview_min_unit)
    public TextView textViewMinUnit;

    @BindView(R.id.textview_scrubbed_value)
    public TextView textViewScrubbedValue;

    @BindView(R.id.textview_scrubbed_unit)
    public TextView textViewScrubbedUnit;

    @BindView(R.id.textview_scrubbed_datetime)
    public TextView textViewScrubbedDateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupSpinnerType();
        setupSpinnerInterval();
        setupSparkView();
    }

    @Override
    protected void load() {
        presenter.data();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {

    }

    @Override
    public void onTemperature(@NonNull List<Temperature> data) {
        sparkView.setLineColor(getResources().getColor(R.color.red_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.red_500_50, null));

        sparkAdapter.setData(data);
    }

    @Override
    public void onHumidity(@NonNull List<Humidity> data) {
        sparkView.setLineColor(getResources().getColor(R.color.blue_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.blue_500_50, null));

        sparkAdapter.setData(data);
    }

    @Override
    public void onPressure(@NonNull List<Pressure> data) {
        sparkView.setLineColor(getResources().getColor(R.color.amber_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.amber_500_50, null));

        sparkAdapter.setData(data);
    }

    @Override
    public void onAirQuality(@NonNull List<AirQuality> data) {
        sparkView.setLineColor(getResources().getColor(R.color.light_green_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.light_green_500_50, null));

        sparkAdapter.setData(data);
    }

    @Override
    public void onMotion(@NonNull List<Motion> data) {
        sparkView.setLineColor(getResources().getColor(R.color.brown_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.brown_500_50, null));

        sparkAdapter.setData(data);
    }

    @Override
    public void onMinMax(int labelUnit, @NonNull String maximum, @NonNull String minimum) {
        textViewMaxUnit.setText(requireContext().getString(labelUnit));
        textViewMinUnit.setText(requireContext().getString(labelUnit));

        textViewMaxValue.setText(maximum);
        textViewMinValue.setText(minimum);
    }

    @Override
    public void applyTopPadding(final float maxDataValue, final float maxValue) {
        sparkView.setPadding(
            sparkView.getPaddingLeft(),
            maxDataValue == maxValue ? 0 : Math.round(sparkView.getHeight() - (sparkView.getHeight() * (maxDataValue / maxValue))),
            sparkView.getPaddingRight(),
            sparkView.getPaddingBottom()
        );
    }

    @Override
    public void noScrubbedValue() {
        textViewScrubbedValue.setVisibility(View.INVISIBLE);
        textViewScrubbedUnit.setVisibility(View.INVISIBLE);
        textViewScrubbedDateTime.setVisibility(View.INVISIBLE);

        textViewScrubbedValue.setText(null);
        textViewScrubbedUnit.setText(null);
        textViewScrubbedDateTime.setText(null);
    }

    @Override
    public void onScrubbed(@Nullable Object value) {
        presenter.scrub(value);
    }

    @Override
    public void hasScrubbedValue(@NonNull final String value, @StringRes final int unit, @NonNull final String dateTime) {
        textViewScrubbedValue.setText(value);
        textViewScrubbedUnit.setText(getString(unit));
        textViewScrubbedDateTime.setText(dateTime);

        textViewScrubbedValue.setVisibility(View.VISIBLE);
        textViewScrubbedUnit.setVisibility(View.VISIBLE);
        textViewScrubbedDateTime.setVisibility(View.VISIBLE);
    }

    @OnItemSelected(value = {R.id.spinner_type, R.id.spinner_interval}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> parent, int position) {
        switch (parent.getId()) {
            case R.id.spinner_type:
                presenter.type(position);
                break;
            case R.id.spinner_interval:
                presenter.interval(position);
                break;
        }
    }

    private void setupToolbar() {
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(this));
    }

    private void setupSpinnerType() {
        final ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(requireContext(), R.array.chart_types, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner);

        spinnerType.setAdapter(adapter);
    }

    private void setupSpinnerInterval() {
        final ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(requireContext(), R.array.chart_intervals, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner);

        spinnerInterval.setAdapter(adapter);
    }

    private void setupSparkView() {
        sparkView.setAdapter(sparkAdapter);

        final Paint baseLinePaint = sparkView.getBaseLinePaint();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[] {16, 16}, 0);
        baseLinePaint.setPathEffect(dashPathEffect);

        sparkView.setSparkAnimator(new MorphSparkAnimator());
        sparkView.setScrubListener(this);
    }
}

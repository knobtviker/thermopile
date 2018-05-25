package com.knobtviker.thermopile.presentation.fragments;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ChartsPresenter;
import com.knobtviker.thermopile.presentation.views.adapters.ChartAdapter;
import com.knobtviker.thermopile.presentation.views.spark.SparkView;
import com.knobtviker.thermopile.presentation.views.spark.animation.MorphSparkAnimator;

import java.util.List;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bojan on 15/06/2017.
 */

public class ChartsFragment extends BaseFragment<ChartsContract.Presenter> implements ChartsContract.View, AdapterView.OnItemSelectedListener {
    public static final String TAG = ChartsFragment.class.getSimpleName();

    private int type = 0;
    private int interval = 0;

    private ChartAdapter<SingleModel> sparkAdapter;

    @BindView(R.id.spinner_type)
    public Spinner spinnerType;

    @BindView(R.id.spinner_interval)
    public Spinner spinnerInterval;

    @BindView(R.id.sparkview)
    public SparkView sparkView;


    public ChartsFragment() {
        presenter = new ChartsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerType();
        setupSpinnerInterval();
        setupSparkView();
    }

    @Override
    public void onResume() {
        super.onResume();

        data();
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

    @Override
    public void onTemperature(@NonNull List<Temperature> data) {
        sparkView.setLineColor(getResources().getColor(R.color.red_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.red_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(5.0f);
    }

    @Override
    public void onHumidity(@NonNull List<Humidity> data) {
        sparkView.setLineColor(getResources().getColor(R.color.blue_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.blue_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(0.0f);
    }

    @Override
    public void onPressure(@NonNull List<Pressure> data) {
        sparkView.setLineColor(getResources().getColor(R.color.amber_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.amber_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(900.0f);
    }

    @Override
    public void onAirQuality(@NonNull List<AirQuality> data) {
        sparkView.setLineColor(getResources().getColor(R.color.light_green_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.light_green_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(0.0f);
    }

    @Override
    public void onMotion(@NonNull List<Acceleration> data) {
        sparkView.setLineColor(getResources().getColor(R.color.brown_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.brown_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(0.0f);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_type:
                setType(position);
                break;
            case R.id.spinner_interval:
                setInterval(position);
                break;
        }

        data();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupSpinnerType() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.chart_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(this);
    }

    private void setupSpinnerInterval() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.chart_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInterval.setAdapter(adapter);
        spinnerInterval.setOnItemSelectedListener(this);
    }

    private void setupSparkView() {
        sparkAdapter = new ChartAdapter<>();
        sparkView.setAdapter(sparkAdapter);

        final Paint baseLinePaint = sparkView.getBaseLinePaint();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{16, 16}, 0);
        baseLinePaint.setPathEffect(dashPathEffect);

        sparkView.setSparkAnimator(new MorphSparkAnimator());
    }


    private void setType(@NonNull final int typeItemPosition) {
        this.type = typeItemPosition;
    }

    private void setInterval(@NonNull final int intervalItemPosition) {
        this.interval = intervalItemPosition;
    }

    private void data() {
        presenter.data(type, interval);
    }
}

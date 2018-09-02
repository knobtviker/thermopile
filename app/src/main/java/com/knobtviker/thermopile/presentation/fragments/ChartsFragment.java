package com.knobtviker.thermopile.presentation.fragments;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Motion;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.shared.SingleModel;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultChartType;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotion;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressure;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartInterval;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartType;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAirQuality;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredHumidity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredPressure;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.views.adapters.ChartAdapter;
import com.knobtviker.thermopile.presentation.views.spark.SparkView;
import com.knobtviker.thermopile.presentation.views.spark.animation.MorphSparkAnimator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.OnItemSelected;

/**
 * Created by bojan on 15/06/2017.
 */

public class ChartsFragment extends BaseFragment<ChartsContract.Presenter> implements ChartsContract.View, SparkView.OnScrubListener {

    private ChartAdapter<SingleModel> sparkAdapter;

    private Interval interval;

    @Inject
    @DefaultFormatDate
    @FormatDate
    String formatDate;

    @Inject
    @DefaultFormatTime
    @FormatTime
    String formatTime;

    @Inject
    @DefaultTemperature
    @UnitTemperature
    int unitTemperature;

    @Inject
    @DefaultPressure
    @UnitPressure
    int unitPressure;

    @Inject
    @DefaultMotion
    @UnitAcceleration
    int unitMotion;

    @Inject
    @DefaultChartType
    @ChartType
    int type;

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

    private String textViewTemperatureUnit;
    private String textViewPressureUnit;
    private String textViewMotionUnit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTemperatureUnit();
        setPressureUnit();
        setMotionUnit();

        setupToolbar();
        setupSpinnerType();
        setupSpinnerInterval();
        setupSparkView();
    }

    @Override
    protected void load() {
        data();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {

    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        formatDate = settings.formatDate;
        formatTime = settings.formatTime;
        unitTemperature = settings.unitTemperature;
        unitPressure = settings.unitPressure;
        unitMotion = settings.unitMotion;

        setTemperatureUnit();
        setPressureUnit();
        setMotionUnit();

        switch (type) {
            case ChartType.TEMPERATURE:
                setMinMaxUnit(textViewTemperatureUnit);
                setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MAXIMUM))), String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MINIMUM))));
                break;
            case ChartType.HUMIDITY:
                setMinMaxUnit(getString(R.string.unit_humidity_percent));
                setMinMaxValue(String.valueOf(MathKit.round(MeasuredHumidity.MAXIMUM)), String.valueOf(MathKit.round(MeasuredHumidity.MINIMUM)));
                break;
            case ChartType.PRESSURE:
                setMinMaxUnit(textViewPressureUnit);
                setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MAXIMUM))), String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MINIMUM))));
                break;
            case ChartType.AIR_QUALITY:
                setMinMaxUnit("");
                setMinMaxValue("Good", "Very bad");
                break;
            case ChartType.MOTION:
                setMinMaxUnit(textViewMotionUnit);
                setMinMaxValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MAXIMUM))), String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MINIMUM))));
                break;
        }
    }

    @Override
    public void onTemperature(@NonNull List<Temperature> data) {
        sparkView.setLineColor(getResources().getColor(R.color.red_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.red_500_50, null));

        setMinMaxUnit(textViewTemperatureUnit);
        setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MAXIMUM))), String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MINIMUM))));

        final Optional<Temperature> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredTemperature.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(MeasuredTemperature.MAXIMUM), MeasuredTemperature.MAXIMUM);

        sparkAdapter.setData(data);
    }

    @Override
    public void onHumidity(@NonNull List<Humidity> data) {
        sparkView.setLineColor(getResources().getColor(R.color.blue_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.blue_500_50, null));

        setMinMaxUnit(getString(R.string.unit_humidity_percent));
        setMinMaxValue(String.valueOf(MathKit.round(MeasuredHumidity.MAXIMUM)), String.valueOf(MathKit.round(MeasuredHumidity.MINIMUM)));

        final Optional<Humidity> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredHumidity.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(MeasuredHumidity.MAXIMUM), MeasuredHumidity.MAXIMUM);

        sparkAdapter.setData(data);
    }

    @Override
    public void onPressure(@NonNull List<Pressure> data) {
        sparkView.setLineColor(getResources().getColor(R.color.amber_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.amber_500_50, null));

        setMinMaxUnit(textViewPressureUnit);
        setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MAXIMUM))), String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MINIMUM))));

        final Optional<Pressure> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredPressure.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(MeasuredPressure.MAXIMUM), MeasuredPressure.MAXIMUM);

        sparkAdapter.setData(data);
    }

    @Override
    public void onAirQuality(@NonNull List<AirQuality> data) {
        sparkView.setLineColor(getResources().getColor(R.color.light_green_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.light_green_500_50, null));

        data.forEach(item -> item.value = ((MeasuredAirQuality.MAXIMUM - item.value) / MeasuredAirQuality.MAXIMUM));


        setMinMaxUnit("");
        setMinMaxValue("Good", "Very bad");

        final Optional<AirQuality> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredAirQuality.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> (MeasuredAirQuality.MAXIMUM - item.value)).orElse(MeasuredAirQuality.MAXIMUM), MeasuredAirQuality.MAXIMUM);

        sparkAdapter.setData(data);
    }

    @Override
    public void onMotion(@NonNull List<Acceleration> data) {
        sparkView.setLineColor(getResources().getColor(R.color.brown_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.brown_500_50, null));

        final List<Motion> motionData = data
            .stream()
            .map(item ->
                Motion.create(
                    item.id,
                    item.vendor,
                    item.name,
                    ((float) Math.min(Math.sqrt(Math.pow(item.valueX, 2) + Math.pow(item.valueY, 2) + Math.pow(item.valueZ + SensorManager.GRAVITY_EARTH, 2)), MeasuredAcceleration.MAXIMUM)),
                    item.timestamp
                ))
            .filter(item -> (item.value >= MeasuredAcceleration.MINIMUM && item.value <= MeasuredAcceleration.MAXIMUM))
            .collect(Collectors.toList());


        setMinMaxUnit(textViewMotionUnit);
        setMinMaxValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MAXIMUM))), String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MINIMUM))));

        final Optional<Motion> optional = motionData
            .stream()
            .filter(item -> item.value <= MeasuredAcceleration.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(MeasuredAcceleration.MAXIMUM), MeasuredAcceleration.MAXIMUM);

        sparkAdapter.setData(motionData);
    }

    @Override
    public void onScrubbed(@Nullable Object value) {
        if (value == null) {
            noScrubbedValue();
        } else {
            final SingleModel object = (SingleModel) value;
            switch (type) {
                case ChartType.TEMPERATURE:
                    hasScrubbedValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, object.value))), textViewTemperatureUnit, object.timestamp);
                    break;
                case ChartType.HUMIDITY:
                    hasScrubbedValue(String.valueOf(MathKit.round(object.value)), getString(R.string.unit_humidity_percent), object.timestamp);
                    break;
                case ChartType.PRESSURE:
                    hasScrubbedValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, object.value))), textViewPressureUnit, object.timestamp);
                    break;
                case ChartType.AIR_QUALITY:
                    hasScrubbedValueWithoutUnit(MathKit.convertIAQValueToLabel(object.value), object.timestamp);
                    break;
                case ChartType.MOTION:
                    hasScrubbedValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, object.value))), textViewMotionUnit, object.timestamp);
                    break;
            }
        }
    }

    @OnItemSelected(value = {R.id.spinner_type, R.id.spinner_interval}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> parent, int position) {
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

    private void setupToolbar() {
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(this));
    }

    private void setupSpinnerType() {
        setType(ChartType.TEMPERATURE);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.chart_types, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner);

        spinnerType.setAdapter(adapter);
    }

    private void setupSpinnerInterval() {
        setInterval(ChartInterval.TODAY);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.chart_intervals, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner);

        spinnerInterval.setAdapter(adapter);
    }

    private void setupSparkView() {
        sparkAdapter = new ChartAdapter<>();
        sparkView.setAdapter(sparkAdapter);

        final Paint baseLinePaint = sparkView.getBaseLinePaint();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{16, 16}, 0);
        baseLinePaint.setPathEffect(dashPathEffect);

        sparkView.setSparkAnimator(new MorphSparkAnimator());
        sparkView.setScrubListener(this);
    }


    private void setType(@ChartType final int typeItemPosition) {
        this.type = typeItemPosition;
    }

    private void setInterval(@ChartInterval final int intervalItemPosition) {
        this.interval = intervalForType(intervalItemPosition);
    }

    private void data() {
        presenter.data(type, interval.getStart().toEpochMilli(), interval.getEnd().toEpochMilli());
    }

    private Interval intervalForType(@ChartInterval final int interval) {
        switch (interval) {
            case ChartInterval.TODAY:
                return DateTimeKit.today();
            case ChartInterval.YESTERDAY:
                return DateTimeKit.yesterday();
            case ChartInterval.THIS_WEEK:
                return DateTimeKit.thisWeek();
            case ChartInterval.LAST_WEEK:
                return DateTimeKit.lastWeek();
            case ChartInterval.THIS_MONTH:
                return DateTimeKit.thisMonth();
            case ChartInterval.LAST_MONTH:
                return DateTimeKit.lastMonth();
            case ChartInterval.THIS_YEAR:
                return DateTimeKit.thisYear();
            case ChartInterval.LAST_YEAR:
                return DateTimeKit.lastYear();
            default:
                return DateTimeKit.lastSecond();
        }
    }

    private void hasScrubbedValue(@NonNull final String value, @Nullable final String unit, final long timestamp) {
        textViewScrubbedValue.setText(value);
        textViewScrubbedUnit.setText(unit);
        textViewScrubbedDateTime.setText(
            DateTimeKit.format(timestamp, String.format("%s %s", formatDate, formatTime))
        );

        textViewScrubbedValue.setVisibility(View.VISIBLE);
        textViewScrubbedUnit.setVisibility(View.VISIBLE);
        textViewScrubbedDateTime.setVisibility(View.VISIBLE);
    }

    private void hasScrubbedValueWithoutUnit(@NonNull final String value, final long timestamp) {
        hasScrubbedValue(value, null, timestamp);
    }

    private void noScrubbedValue() {
        textViewScrubbedValue.setVisibility(View.INVISIBLE);
        textViewScrubbedUnit.setVisibility(View.INVISIBLE);
        textViewScrubbedDateTime.setVisibility(View.INVISIBLE);

        textViewScrubbedValue.setText(null);
        textViewScrubbedUnit.setText(null);
        textViewScrubbedDateTime.setText(null);
    }

    private void setTemperatureUnit() {
        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                textViewTemperatureUnit = getString(R.string.unit_temperature_celsius);
                break;
            case UnitTemperature.FAHRENHEIT:
                textViewTemperatureUnit = getString(R.string.unit_temperature_fahrenheit);
                break;
            case UnitTemperature.KELVIN:
                textViewTemperatureUnit = getString(R.string.unit_temperature_kelvin);
                break;
            default:
                textViewTemperatureUnit = getString(R.string.unit_temperature_celsius);
                break;
        }
    }

    private void setPressureUnit() {
        switch (unitPressure) {
            case UnitPressure.PASCAL:
                textViewPressureUnit = getString(R.string.unit_pressure_pascal);
                break;
            case UnitPressure.BAR:
                textViewPressureUnit = getString(R.string.unit_pressure_bar);
                break;
            case UnitPressure.PSI:
                textViewPressureUnit = getString(R.string.unit_pressure_psi);
                break;
            default:
                textViewPressureUnit = getString(R.string.unit_pressure_pascal);
                break;
        }
    }

    private void setMotionUnit() {
        switch (unitMotion) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                textViewMotionUnit = getString(R.string.unit_acceleration_ms2);
                break;
            case UnitAcceleration.G:
                textViewMotionUnit = getString(R.string.unit_acceleration_g);
                break;
            case UnitAcceleration.GAL:
                textViewMotionUnit = getString(R.string.unit_acceleration_gal);
                break;
            case UnitAcceleration.CENTIMETERS_PER_SECOND_2:
                textViewMotionUnit = getString(R.string.unit_acceleration_cms2);
                break;
            default:
                textViewMotionUnit = getString(R.string.unit_acceleration_ms2);
                break;
        }
    }

    private void setMinMaxUnit(@NonNull final String unit) {
        textViewMaxUnit.setText(unit);
        textViewMinUnit.setText(unit);
    }

    private void setMinMaxValue(@NonNull final String maxValue, @NonNull final String minValue) {
        textViewMaxValue.setText(maxValue);
        textViewMinValue.setText(minValue);
    }

    private void applyTopPadding(final float maxDataValue, final float maxValue) {
        sparkView.setPadding(
            sparkView.getPaddingLeft(),
            maxDataValue == maxValue ? 0 : Math.round(sparkView.getHeight() - (sparkView.getHeight() * (maxDataValue / maxValue))),
            sparkView.getPaddingRight(),
            sparkView.getPaddingBottom()
        );
    }
}

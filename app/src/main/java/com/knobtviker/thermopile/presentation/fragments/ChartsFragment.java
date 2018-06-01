package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
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
import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ChartsPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.views.adapters.ChartAdapter;
import com.knobtviker.thermopile.presentation.views.spark.SparkView;
import com.knobtviker.thermopile.presentation.views.spark.animation.MorphSparkAnimator;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class ChartsFragment extends BaseFragment<ChartsContract.Presenter> implements ChartsContract.View, AdapterView.OnItemSelectedListener, SparkView.OnScrubListener {
    public static final String TAG = ChartsFragment.class.getSimpleName();

    private String formatDate;
    private String formatTime;
    private int unitTemperature;
    private int unitPressure;
    private int unitMotion;

    private int type;
    private int intervalPosition;
    private Interval interval;

    private ChartAdapter<SingleModel> sparkAdapter;

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

    public ChartsFragment() {
        formatDate = Constants.DEFAULT_FORMAT_DATE;
        formatTime = Constants.FORMAT_TIME_LONG_24H;
        unitTemperature = Constants.UNIT_TEMPERATURE_CELSIUS;
        unitPressure = Constants.UNIT_PRESSURE_PASCAL;
        unitMotion = Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2;

        presenter = new ChartsPresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setTemperatureUnit();
        setPressureUnit();
        setMotionUnit();
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
            case 0:
                setMinMaxUnit(textViewTemperatureUnit);
                setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, Constants.MEASURED_TEMPERATURE_MAX))), String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, Constants.MEASURED_TEMPERATURE_MIN))));
                break;
            case 1:
                setMinMaxUnit(getString(R.string.unit_humidity_percent));
                setMinMaxValue(String.valueOf(MathKit.round(Constants.MEASURED_HUMIDITY_MAX)), String.valueOf(MathKit.round(Constants.MEASURED_HUMIDITY_MIN)));
                break;
            case 2:
                setMinMaxUnit(textViewPressureUnit);
                setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, Constants.MEASURED_PRESSURE_MAX))), String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, Constants.MEASURED_PRESSURE_MIN))));
                break;
            case 3:
                setMinMaxUnit("");
                setMinMaxValue("Good", "Very bad");
                break;
            case 4:
                setMinMaxUnit(textViewMotionUnit);
                setMinMaxValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, Constants.MEASURED_MOTION_MAX))), String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, Constants.MEASURED_MOTION_MIN))));
                break;
        }
    }

    @Override
    public void onTemperature(@NonNull List<Temperature> data) {
        sparkView.setLineColor(getResources().getColor(R.color.red_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.red_500_50, null));

        setMinMaxUnit(textViewTemperatureUnit);
        setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, Constants.MEASURED_TEMPERATURE_MAX))), String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, Constants.MEASURED_TEMPERATURE_MIN))));

        final Optional<Temperature> optional = data
            .stream()
            .filter(item -> item.value <= Constants.MEASURED_TEMPERATURE_MAX)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(Constants.MEASURED_TEMPERATURE_MAX), Constants.MEASURED_TEMPERATURE_MAX);

        sparkAdapter.setData(data);
    }

    @Override
    public void onHumidity(@NonNull List<Humidity> data) {
        sparkView.setLineColor(getResources().getColor(R.color.blue_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.blue_500_50, null));

        setMinMaxUnit(getString(R.string.unit_humidity_percent));
        setMinMaxValue(String.valueOf(MathKit.round(Constants.MEASURED_HUMIDITY_MAX)), String.valueOf(MathKit.round(Constants.MEASURED_HUMIDITY_MIN)));

        final Optional<Humidity> optional = data
            .stream()
            .filter(item -> item.value <= Constants.MEASURED_HUMIDITY_MAX)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(Constants.MEASURED_HUMIDITY_MAX), Constants.MEASURED_HUMIDITY_MAX);

        sparkAdapter.setData(data);
    }

    @Override
    public void onPressure(@NonNull List<Pressure> data) {
        sparkView.setLineColor(getResources().getColor(R.color.amber_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.amber_500_50, null));

        setMinMaxUnit(textViewPressureUnit);
        setMinMaxValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, Constants.MEASURED_PRESSURE_MAX))), String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, Constants.MEASURED_PRESSURE_MIN))));

        final Optional<Pressure> optional = data
            .stream()
            .filter(item -> item.value <= Constants.MEASURED_PRESSURE_MAX)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(Constants.MEASURED_PRESSURE_MAX), Constants.MEASURED_PRESSURE_MAX);

        sparkAdapter.setData(data);
    }

    @Override
    public void onAirQuality(@NonNull List<AirQuality> data) {
        sparkView.setLineColor(getResources().getColor(R.color.light_green_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.light_green_500_50, null));

        data.forEach(item -> item.value = ((Constants.MEASURED_AIR_QUALITY_MAX - item.value) / Constants.MEASURED_AIR_QUALITY_MAX));


        setMinMaxUnit("");
        setMinMaxValue("Good", "Very bad");

        final Optional<AirQuality> optional = data
            .stream()
            .filter(item -> item.value <= Constants.MEASURED_AIR_QUALITY_MAX)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> (Constants.MEASURED_AIR_QUALITY_MAX - item.value)).orElse(Constants.MEASURED_AIR_QUALITY_MAX), Constants.MEASURED_AIR_QUALITY_MAX);

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
                    ((float) Math.min(Math.sqrt(Math.pow(item.valueX, 2) + Math.pow(item.valueY, 2) + Math.pow(item.valueZ + SensorManager.GRAVITY_EARTH, 2)), 19.61330000)), //TODO: This hardcoded value must be set according to selected unit value for acceleration in Settings
                    item.timestamp
                ))
            .filter(item -> (item.value >= Constants.MEASURED_MOTION_MIN && item.value <= Constants.MEASURED_MOTION_MAX))
            .collect(Collectors.toList());


        setMinMaxUnit(textViewMotionUnit);
        setMinMaxValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, Constants.MEASURED_MOTION_MAX))), String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, Constants.MEASURED_MOTION_MIN))));

        final Optional<Motion> optional = motionData
            .stream()
            .filter(item -> item.value <= Constants.MEASURED_MOTION_MAX)
            .max((item, other) -> Float.compare(item.value, other.value));

        applyTopPadding(optional.map(item -> item.value).orElse(Constants.MEASURED_MOTION_MAX), Constants.MEASURED_MOTION_MAX);

        sparkAdapter.setData(motionData);
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
        setType(0);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.chart_types, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(this);

    }

    private void setupSpinnerInterval() {
        setInterval(0);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.chart_intervals, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

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
        sparkView.setScrubListener(this);
    }


    private void setType(final int typeItemPosition) {
        this.type = typeItemPosition;
    }

    private void setInterval(final int intervalItemPosition) {
        this.intervalPosition = intervalItemPosition;
        this.interval = intervalForType(intervalItemPosition);
    }

    private void data() {
        presenter.data(type, interval.getStartMillis(), interval.getEndMillis());
    }

    private Interval intervalForType(final int interval) {
        final DateTime now = DateTime.now();
        final DateTime other;

        switch (interval) {
            case 0: //Today
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                return new Interval(other, now);
            case 1: //Yesterday
                final DateTime today = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                final DateTime yesterday = today.minusDays(1);
                return new Interval(yesterday, today);
            case 2: //This week
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfWeek(1);
                return new Interval(other, now);
            case 3: //Last week
                final DateTime thisWeek = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfWeek(1);
                final DateTime lastWeek = thisWeek.minusWeeks(1);
                return new Interval(lastWeek, thisWeek);
            case 4: //This month
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfMonth(1);
                return new Interval(other, now);
            case 5: //Last month
                final DateTime thisMonth = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfMonth(1);
                final DateTime lastMonth = thisMonth.minusMonths(1);
                return new Interval(lastMonth, thisMonth);
            case 6: //This year
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfYear(1);
                return new Interval(other, now);
            case 7: //Last year
                final DateTime thisYear = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfYear(1);
                final DateTime lastYear = thisYear.minusYears(1);
                return new Interval(lastYear, thisYear);
            default:
                return new Interval(now.minusMillis(1), now);
        }
    }

    @Override
    public void onScrubbed(@Nullable Object value) {
        if (value == null) {
            noScrubbedValue();
        } else {
            final SingleModel object = (SingleModel) value;
            switch (type) {
                case 0:
                    hasScrubbedValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, object.value))), textViewTemperatureUnit, object.timestamp);
                    break;
                case 1:
                    hasScrubbedValue(String.valueOf(MathKit.round(object.value)), getString(R.string.unit_humidity_percent), object.timestamp);
                    break;
                case 2:
                    hasScrubbedValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, object.value))), textViewPressureUnit, object.timestamp);
                    break;
                case 3:
                    hasScrubbedValueWithoutUnit(convertIAQValueToLabel(object.value), object.timestamp);
                    break;
                case 4:
                    hasScrubbedValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, object.value))), textViewMotionUnit, object.timestamp);
                    break;
            }
        }
    }

    private void hasScrubbedValue(@NonNull final String value, @Nullable final String unit, final long timestamp) {
        textViewScrubbedValue.setText(value);
        textViewScrubbedUnit.setText(unit);
        textViewScrubbedDateTime.setText(new DateTime(timestamp).toString(String.format("%s %s", formatDate, formatTime)));

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
            case Constants.UNIT_TEMPERATURE_CELSIUS:
                textViewTemperatureUnit = getString(R.string.unit_temperature_celsius);
                break;
            case Constants.UNIT_TEMPERATURE_FAHRENHEIT:
                textViewTemperatureUnit = getString(R.string.unit_temperature_fahrenheit);
                break;
            case Constants.UNIT_TEMPERATURE_KELVIN:
                textViewTemperatureUnit = getString(R.string.unit_temperature_kelvin);
                break;
            default:
                textViewTemperatureUnit = getString(R.string.unit_temperature_celsius);
                break;
        }
    }

    private void setPressureUnit() {
        switch (unitPressure) {
            case Constants.UNIT_PRESSURE_PASCAL:
                textViewPressureUnit = getString(R.string.unit_pressure_pascal);
                break;
            case Constants.UNIT_PRESSURE_BAR:
                textViewPressureUnit = getString(R.string.unit_pressure_bar);
                break;
            case Constants.UNIT_PRESSURE_PSI:
                textViewPressureUnit = getString(R.string.unit_pressure_psi);
                break;
            default:
                textViewPressureUnit = getString(R.string.unit_pressure_pascal);
                break;
        }
    }

    private void setMotionUnit() {
        switch (unitMotion) {
            case Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2:
                textViewMotionUnit = getString(R.string.unit_acceleration_ms2);
                break;
            case Constants.UNIT_ACCELERATION_G:
                textViewMotionUnit = getString(R.string.unit_acceleration_g);
                break;
            default:
                textViewMotionUnit = getString(R.string.unit_acceleration_ms2);
                break;
        }
    }

    //TODO: Move this somewhere else and cleanup strings
    private String convertIAQValueToLabel(final float value) {
        if (value >= 401 && value <= 500) {
            return "Very bad";
        } else if (value >= 301 && value <= 400) {
            return "Worse";
        } else if (value >= 201 && value <= 300) {
            return "Bad";
        } else if (value >= 101 && value <= 200) {
            return "Little bad";
        } else if (value >= 51 && value <= 100) {
            return "Average";
        } else if (value >= 0 && value <= 50) {
            return "Good";
        } else {
            return "Unknown";
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
        Timber.i("applyTopPadding -> maxDataValue: %f - maxValue: %f - sparkView.getHeight(): %d - paddingTop: %d", maxDataValue, maxValue, sparkView.getHeight(), Math.round(sparkView.getHeight() - (sparkView.getHeight() * (maxDataValue / maxValue))));
        sparkView.setPadding(
            sparkView.getPaddingLeft(),
            maxDataValue == maxValue ? 0 : Math.round(sparkView.getHeight() - (sparkView.getHeight() * (maxDataValue / maxValue))),
            sparkView.getPaddingRight(),
            sparkView.getPaddingBottom()
        );
    }
}

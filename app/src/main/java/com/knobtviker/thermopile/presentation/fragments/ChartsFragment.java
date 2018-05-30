package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ChartsPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.adapters.ChartAdapter;
import com.knobtviker.thermopile.presentation.views.spark.SparkView;
import com.knobtviker.thermopile.presentation.views.spark.animation.MorphSparkAnimator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.List;

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

    @BindView(R.id.textview_scrubbed)
    public TextView textViewScrubbed;

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
    }

    @Override
    public void onTemperature(@NonNull List<Temperature> data) {
        maybeAdjustRightPadding(data.get(0).timestamp, data.get(data.size() - 1).timestamp);

        sparkView.setLineColor(getResources().getColor(R.color.red_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.red_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(5.0f);
    }

    @Override
    public void onHumidity(@NonNull List<Humidity> data) {
        maybeAdjustRightPadding(data.get(0).timestamp, data.get(data.size() - 1).timestamp);

        sparkView.setLineColor(getResources().getColor(R.color.blue_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.blue_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(0.0f);
    }

    @Override
    public void onPressure(@NonNull List<Pressure> data) {
        maybeAdjustRightPadding(data.get(0).timestamp, data.get(data.size() - 1).timestamp);

        sparkView.setLineColor(getResources().getColor(R.color.amber_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.amber_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(900.0f);
    }

    @Override
    public void onAirQuality(@NonNull List<AirQuality> data) {
        maybeAdjustRightPadding(data.get(0).timestamp, data.get(data.size() - 1).timestamp);

        sparkView.setLineColor(getResources().getColor(R.color.light_green_500, null));
        sparkView.setFillColor(getResources().getColor(R.color.light_green_500_50, null));
        sparkAdapter.setData(data);
        sparkAdapter.setBaseline(0.0f);
    }

    @Override
    public void onMotion(@NonNull List<Acceleration> data) {
        maybeAdjustRightPadding(data.get(0).timestamp, data.get(data.size() - 1).timestamp);

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
        setType(0);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.chart_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(this);

    }

    private void setupSpinnerInterval() {
        setInterval(0);

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

    private void maybeAdjustRightPadding(final long firstTimestamp, final long lastTimestamp) {
        final int chartWidth = getResources().getDisplayMetrics().widthPixels - Math.round(guidelineSplit.getX());

        final long fullIntervalMillis = interval.toDurationMillis();
        final long leftGapMillis = firstTimestamp - interval.getStartMillis();
        final long rightGapMillis = interval.getEndMillis() - lastTimestamp;

        final float ratioLeft = (float) leftGapMillis / fullIntervalMillis;
        final float ratioRight = (float) rightGapMillis / fullIntervalMillis;
        final int paddingLeft = chartWidth - Math.round(chartWidth * ratioLeft);
        final int paddingRight = chartWidth - Math.round(chartWidth * ratioRight);

        sparkView.setPadding(paddingLeft, sparkView.getPaddingTop(), paddingRight, sparkView.getPaddingBottom());
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
        if (value == null)  {
            noScrubbedValue();
        } else {
            switch (type) {
                case 0:
                    hasScrubbedTemperature((Temperature) value);
                    break;
//                case 1:
//                    (Humidity) value;
//                    break;
//                case 2:
//                    (Pressure) value;
//                    break;
//                case 3:
//                    (AirQuality) value;
//                    break;
//            case 0:
//                ((Acceleration) value)
//                break;
            }
        }
    }

    private void hasScrubbedTemperature(@NonNull final Temperature value) {
        textViewScrubbed.setText(String.format("%.1f%s %s", value.value, textViewTemperatureUnit, new DateTime(value.timestamp).toString(String.format("%s %s", formatDate, formatTime))));
        textViewScrubbed.setVisibility(View.VISIBLE);
    }

    private void noScrubbedValue() {
        textViewScrubbed.setVisibility(View.INVISIBLE);
        textViewScrubbed.setText(null);
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
}

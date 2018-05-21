package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.controllers.PIDController;
import com.knobtviker.thermopile.presentation.views.ArcView;
import com.knobtviker.thermopile.presentation.views.adapters.ThresholdAdapter;
import com.knobtviker.thermopile.presentation.views.listeners.DayScrollListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends BaseFragment<MainContract.Presenter> implements MainContract.View, DayScrollListener.Listener {
    public static final String TAG = MainFragment.class.getSimpleName();

    private ThresholdAdapter thresholdAdapter;

    private LinearLayoutManager linearLayoutManager;

    private DateTimeZone dateTimeZone;
    private int formatClock;
    private String formatDate;
    private String formatTime;
    private int unitTemperature;
    private int unitPressure;
    private int unitMotion;

    private final PIDController pidController;

    @BindView(R.id.textview_date)
    public TextView textViewDate;

    @BindView(R.id.textview_clock)
    public TextClock textViewClock;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.textview_humidity)
    public TextView textViewHumidity;

    @BindView(R.id.textview_pressure)
    public TextView textViewPressure;

    @BindView(R.id.textview_iaq)
    public TextView textViewIAQ;

    @BindView(R.id.textview_motion)
    public TextView textViewMotion;

    @BindView(R.id.arc_temperature)
    public ArcView arcViewTemperature;

    @BindView(R.id.arc_humidity)
    public ArcView arcViewHumidity;

    @BindView(R.id.arc_iaq)
    public ArcView arcViewIAQ;

    @BindView(R.id.arc_pressure)
    public ArcView arcViewPressure;

    @BindView(R.id.arc_motion)
    public ArcView arcViewMotion;

    @BindView(R.id.textview_temperature_unit)
    public TextView textViewTemperatureUnit;

    @BindView(R.id.textview_pressure_unit)
    public TextView textViewPressureUnit;

    @BindView(R.id.textview_motion_unit)
    public TextView textViewMotionUnit;

    @BindView(R.id.textview_day)
    public TextView textViewDay;

    @BindView(R.id.recyclerview_thresholds)
    public RecyclerView recyclerViewThresholds;

    public MainFragment() {
        dateTimeZone = DateTimeZone.forID(Constants.DEFAULT_TIMEZONE);
        formatClock = Constants.CLOCK_MODE_24H;
        formatDate = Constants.DEFAULT_FORMAT_DATE;
        formatTime = Constants.FORMAT_TIME_LONG_24H;
        unitTemperature = Constants.UNIT_TEMPERATURE_CELSIUS;
        unitPressure = Constants.UNIT_PRESSURE_PASCAL;
        unitMotion = Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2;

        presenter = new MainPresenter(this);

        pidController = new PIDController(40, 1000);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        presenter.observeDateChanged(view.getContext());
    }

    @Override
    public void onResume() {
        data();
        thresholds();

        super.onResume();
    }

    @Override
    public void showLoading(boolean isLoading) {
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTemperatureChanged(final float value) {
        arcViewTemperature.setProgress(value / Constants.MEASURED_TEMPERATURE_MAX);
        textViewTemperature.setText(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))));

//        pidController.doPID(Math.round(value));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onHumidityChanged(final float value) {
        arcViewHumidity.setProgress(value / Constants.MEASURED_HUMIDITY_MAX);
        textViewHumidity.setText(String.valueOf(MathKit.round(value)));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPressureChanged(final float value) {
        arcViewPressure.setProgress(value / Constants.MEASURED_PRESSURE_MAX);
        textViewPressure.setText(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, value))));
    }

    @Override
    public void onAirQualityChanged(final float value) {
        final Pair<String, Integer> pair = convertIAQValueToLabelAndColor(value);

        textViewIAQ.setText(pair.first);
        arcViewIAQ.setProgressColor(pair.second);
        arcViewIAQ.setProgress((Constants.MEASURED_AIR_QUALITY_MAX - value) / Constants.MEASURED_AIR_QUALITY_MAX);
    }

    // Calculate pitch, roll, and heading.
    // Pitch/roll calculations take from this app note:
    // http://cache.freescale.com/files/sensors/doc/app_note/AN3461.pdf?fpsp=1
    // Heading calculations taken from this app note:
    // http://www51.honeywell.com/aero/common/documents/myaerospacecatalog-documents/Defense_Brochures-documents/Magnetic__Literature_Application_notes-documents/AN203_Compass_Heading_Using_Magnetometers.pdf
    // The LSM9DS1's mag x and y axes are opposite to the accelerometer,
    // so my, mx are substituted for each other.
    // Pay attention what the position of the sensor is ato its hardcoded axes, hence the index shuffle.
//    private void onMotionChanged(final float[] acceleration, final float[] angularVelocity, final float[] magneticField) {
    @Override
    public void onAccelerationChanged(final float[] values) {
        final double ax = values[0];
        final double ay = values[2];
        final double az = values[1]; //možda minus
//
//        final double mx = -magneticField[2];
//        final double my = -magneticField[0];
//        final double mz = magneticField[1];
//
//        double roll = Math.atan2(ay, az);
//
//        double pitch = Math.atan2(-ax, Math.sqrt(Math.pow(ay, 2) + Math.pow(az, 2)));
//
//        double heading;
//        if (my == 0) {
//            heading = (mx < 0) ? Math.PI : 0;
//        } else {
//            heading = Math.atan2(mx, my);
//        }
//
//        heading -= Constants.DECLINATION * Math.PI / 180;
//
//        if (heading > Math.PI) {
//            heading -= (2 * Math.PI);
//        } else if (heading < -Math.PI) {
//            heading += (2 * Math.PI);
//        } else if (heading < 0) {
//            heading += 2 * Math.PI;
//        }
//
//        // Convert everything from radians to degrees:
//        heading *= 180.0 / Math.PI;
//        pitch *= 180.0 / Math.PI;
//        roll *= 180.0 / Math.PI;

        //Peak ground acceleration calculation
        final float value = (float) Math.min(Math.sqrt(Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az + SensorManager.GRAVITY_EARTH, 2)), 19.61330000);
        arcViewMotion.setProgress(value / 19.61330000f); //TODO: This hardcoded value must be set according to selected unit value for acceleration in Settings
        textViewMotion.setText(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, value))));
    }

    @Override
    public void onDateChanged() {
        setDate();
        thresholds();
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        dateTimeZone = DateTimeZone.forID(settings.timezone);
        formatClock = settings.formatClock;
        formatDate = settings.formatDate;
        formatTime = settings.formatTime;
        unitTemperature = settings.unitTemperature;
        unitPressure = settings.unitPressure;
        unitMotion = settings.unitMotion;

        setFormatClock();
        setTemperatureUnit();
        setPressureUnit();
        setMotionUnit();
        setDate();

        ((ThermopileApplication)getActivity().getApplication()).refresh();
    }

    @Override
    public void onThresholdsChanged(@NonNull ImmutableList<ThresholdInterval> thresholdIntervals) {
        thresholdAdapter.setData(thresholdIntervals);
    }

    @Override
    public void onDayChanged() {
        if (thresholdAdapter.getItemCount() > 0) {
            textViewDay.setText(thresholdAdapter.getItemDay(linearLayoutManager.findFirstVisibleItemPosition()));
        } else {
            //TODO: What else?
        }
    }

    @OnClick({R.id.floatingactionbutton_down, R.id.floatingactionbutton_up, R.id.button_charts, R.id.button_schedule, R.id.button_settings})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.floatingactionbutton_down:
//                RelayRawDataSource.getInstance()
//                    .on()
//                    .subscribe();
                break;
            case R.id.floatingactionbutton_up:
//                RelayRawDataSource.getInstance()
//                    .off()
//                    .subscribe();
                break;
            case R.id.button_charts:
                NavHostFragment.findNavController(this).navigate(R.id.action_mainFragment_to_chartsFragment);
                break;
            case R.id.button_schedule:
                NavHostFragment.findNavController(this).navigate(R.id.action_mainFragment_to_scheduleFragment);
                break;
            case R.id.button_settings:
                NavHostFragment.findNavController(this).navigate(R.id.action_mainFragment_to_settingsFragment);
                break;
        }
    }

    private void setupRecyclerView() {
        thresholdAdapter = new ThresholdAdapter(getContext());

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewThresholds.setLayoutManager(linearLayoutManager);
        recyclerViewThresholds.setAdapter(thresholdAdapter);
        recyclerViewThresholds.addOnScrollListener(DayScrollListener.create(this));
    }

    private void setFormatClock() {
        textViewClock.setTimeZone(dateTimeZone.toString());
        if (formatClock == Constants.CLOCK_MODE_12H) {
            textViewClock.setFormat12Hour(formatTime);
            textViewClock.setFormat24Hour(null);
        } else {
            textViewClock.setFormat24Hour(formatTime);
            textViewClock.setFormat12Hour(null);
        }
    }

    private void setTemperatureUnit() {
        switch (unitTemperature) {
            case Constants.UNIT_TEMPERATURE_CELSIUS:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_celsius));
                break;
            case Constants.UNIT_TEMPERATURE_FAHRENHEIT:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_fahrenheit));
                break;
            case Constants.UNIT_TEMPERATURE_KELVIN:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_kelvin));
                break;
            default:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_celsius));
                break;
        }
    }

    private void setPressureUnit() {
        switch (unitPressure) {
            case Constants.UNIT_PRESSURE_PASCAL:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_pascal));
                break;
            case Constants.UNIT_PRESSURE_BAR:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_bar));
                break;
            case Constants.UNIT_PRESSURE_PSI:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_psi));
                break;
            default:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_pascal));
                break;
        }
    }

    private void setMotionUnit() {
        switch (unitMotion) {
            case Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_ms2));
                break;
            case Constants.UNIT_ACCELERATION_G:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_g));
                break;
            default:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_ms2));
                break;
        }
    }

    private void setDate() {
        final DateTime dateTime = new DateTime(dateTimeZone);
        textViewDate.setText(dateTime.toString(formatDate));
    }

    private void data() {
        presenter.observeTemperatureChanged(getContext());
        presenter.observePressureChanged(getContext());
        presenter.observeHumidityChanged(getContext());
        presenter.observeAirQualityChanged(getContext());
        presenter.observeAccelerationChanged(getContext());
        presenter.settings();

        ((ThermopileApplication)getActivity().getApplication()).refresh();
    }

    private void thresholds() {
        presenter.thresholds();
    }

    //TODO: Move this somewhere else and cleanup strings
    private Pair<String, Integer> convertIAQValueToLabelAndColor(final float value) {
        if (value >= 401 && value <= 500) {
            return Pair.create("Very bad", R.color.black);
        } else if (value >= 301 && value <= 400) {
            return Pair.create("Worse", R.color.pink_500);
        } else if (value >= 201 && value <= 300) {
            return Pair.create("Bad", R.color.red_500);
        } else if (value >= 101 && value <= 200) {
            return Pair.create("Little bad", R.color.orange_500);
        } else if (value >= 51 && value <= 100) {
            return Pair.create("Average", R.color.yellow_500);
        } else if (value >= 0 && value <= 50) {
            return Pair.create("Good", R.color.light_green_500);
        } else {
            return Pair.create("Unknown", R.color.light_gray);
        }
    }
}
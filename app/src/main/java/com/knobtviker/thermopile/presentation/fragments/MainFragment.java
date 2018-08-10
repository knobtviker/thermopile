package com.knobtviker.thermopile.presentation.fragments;

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

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAirQuality;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredHumidity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredPressure;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.controllers.PIDController;
import com.knobtviker.thermopile.presentation.views.ArcView;
import com.knobtviker.thermopile.presentation.views.adapters.ThresholdAdapter;
import com.knobtviker.thermopile.presentation.views.listeners.DayScrollListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

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

    @ClockMode
    private int formatClock;

    @FormatDate
    private String formatDate;

    @FormatTime
    private String formatTime;

    @UnitTemperature
    private int unitTemperature;

    @UnitPressure
    private int unitPressure;

    @UnitAcceleration
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
        dateTimeZone = DateTimeZone.forID(Default.TIMEZONE);
        formatClock = ClockMode._24H;
        formatDate = FormatDate.EEEE_DD_MM_YYYY;
        formatTime = FormatTime.HH_MM;
        unitTemperature = UnitTemperature.CELSIUS;
        unitPressure = UnitPressure.PASCAL;
        unitMotion = UnitAcceleration.METERS_PER_SECOND_2;

        pidController = new PIDController(40, 1000);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        presenter = new MainPresenter(this);

        load();
    }

    @Override
    public void showLoading(boolean isLoading) {
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onTemperatureChanged(final float value) {
        arcViewTemperature.setProgress(value / MeasuredTemperature.MAXIMUM);
        textViewTemperature.setText(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))));

//        pidController.doPID(Math.round(value));
    }

    @Override
    public void onHumidityChanged(final float value) {
        arcViewHumidity.setProgress(value / MeasuredHumidity.MAXIMUM);
        textViewHumidity.setText(String.valueOf(MathKit.round(value)));
    }

    @Override
    public void onPressureChanged(final float value) {
        arcViewPressure.setProgress(value / MeasuredPressure.MAXIMUM);
        textViewPressure.setText(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, value))));
    }

    @Override
    public void onAirQualityChanged(final float value) {
        final Pair<String, Integer> pair = MathKit.convertIAQValueToLabelAndColor(value);

        textViewIAQ.setText(pair.first);
        arcViewIAQ.setProgressColor(pair.second);
        arcViewIAQ.setProgress((MeasuredAirQuality.MAXIMUM - value) / MeasuredAirQuality.MAXIMUM);
    }

    @Override
    public void onAccelerationChanged(final float[] values) {
        final double ax = values[0];
        final double ay = values[2] + 0.2f;
        final double az = values[1] + SensorManager.GRAVITY_EARTH; //možda minus

        //ax: -0.10000000149011612 ay: -0.20000000298023224 az: -9.800000190734863 + 9.80665F
        Timber.i("ax: %s ay: %s az: %s", ax, ay, az);

        final float value = (float) Math.min(Math.sqrt(Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2)), MeasuredAcceleration.MAXIMUM);
        arcViewMotion.setProgress(value / MeasuredAcceleration.MAXIMUM); // 2g in m/s2
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

        if (thresholdAdapter != null) {
            thresholdAdapter.setUnitAndFormat(unitTemperature, formatTime, formatDate);

            if (thresholdAdapter.getItemCount() > 0 && linearLayoutManager.findFirstVisibleItemPosition() != -1) {
                textViewDay.setText(thresholdAdapter.getItemDay(linearLayoutManager.findFirstVisibleItemPosition()));
            }
        }

        ((ThermopileApplication) requireActivity().getApplication()).refresh();
    }

    @Override
    public void onThresholdsChanged(@NonNull List<ThresholdInterval> thresholdIntervals) {
        thresholdAdapter.setData(thresholdIntervals);
    }

    @Override
    public void onDayChanged() {
        if (thresholdAdapter.getItemCount() > 0) {
            textViewDay.setText(thresholdAdapter.getItemDay(linearLayoutManager.findFirstVisibleItemPosition()));
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
        thresholdAdapter = new ThresholdAdapter(requireContext(), unitTemperature, formatTime, formatDate);

        linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewThresholds.setLayoutManager(linearLayoutManager);
        recyclerViewThresholds.setAdapter(thresholdAdapter);
        recyclerViewThresholds.addOnScrollListener(DayScrollListener.create(this));
    }

    private void setFormatClock() {
        textViewClock.setTimeZone(dateTimeZone.toString());
        if (formatClock == ClockMode._12H) {
            textViewClock.setFormat12Hour(formatTime);
            textViewClock.setFormat24Hour(null);
        } else {
            textViewClock.setFormat24Hour(formatTime);
            textViewClock.setFormat12Hour(null);
        }
    }

    private void setTemperatureUnit() {
        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_celsius));
                break;
            case UnitTemperature.FAHRENHEIT:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_fahrenheit));
                break;
            case UnitTemperature.KELVIN:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_kelvin));
                break;
            default:
                textViewTemperatureUnit.setText(getString(R.string.unit_temperature_celsius));
                break;
        }
    }

    private void setPressureUnit() {
        switch (unitPressure) {
            case UnitPressure.PASCAL:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_pascal));
                break;
            case UnitPressure.BAR:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_bar));
                break;
            case UnitPressure.PSI:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_psi));
                break;
            default:
                textViewPressureUnit.setText(getString(R.string.unit_pressure_pascal));
                break;
        }
    }

    private void setMotionUnit() {
        switch (unitMotion) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_ms2));
                break;
            case UnitAcceleration.G:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_g));
                break;
            case UnitAcceleration.GAL:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_gal));
                break;
            case UnitAcceleration.CENTIMETERS_PER_SECOND_2:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_cms2));
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

    private void load() {
        date();
        data();
        thresholds();
    }

    private void date() {
        presenter.observeDateChanged(requireContext());
    }

    private void data() {
        presenter.observeTemperature();
        presenter.observePressure();
        presenter.observeHumidity();
        presenter.observeAirQuality();
        presenter.observeAcceleration();
        presenter.settings();

        ((ThermopileApplication) requireActivity().getApplication()).refresh();
    }

    private void thresholds() {
        presenter.thresholds();
    }
}
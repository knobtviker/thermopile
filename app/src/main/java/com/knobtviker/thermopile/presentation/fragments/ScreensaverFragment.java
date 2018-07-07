package com.knobtviker.thermopile.presentation.fragments;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ScreenSaverPresenter;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.constants.ClockMode;
import com.knobtviker.thermopile.presentation.utils.constants.Default;
import com.knobtviker.thermopile.presentation.utils.constants.FormatDate;
import com.knobtviker.thermopile.presentation.utils.constants.FormatDay;
import com.knobtviker.thermopile.presentation.utils.constants.FormatTime;
import com.knobtviker.thermopile.presentation.utils.constants.UnitAcceleration;
import com.knobtviker.thermopile.presentation.utils.constants.UnitPressure;
import com.knobtviker.thermopile.presentation.utils.constants.UnitTemperature;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Objects;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScreensaverFragment extends BaseFragment<ScreenSaverContract.Presenter> implements ScreenSaverContract.View {
    public static final String TAG = ScreensaverFragment.class.getSimpleName();

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

    @BindView(R.id.textview_clock)
    public TextClock textViewClock;

    @BindView(R.id.textview_day)
    public TextView textViewDay;

    @BindView(R.id.textview_date)
    public TextView textViewDate;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.textview_humidity)
    public TextView textViewHumidity;

    @BindView(R.id.textview_pressure)
    public TextView textViewPressure;

    @BindView(R.id.textview_iaq)
    public TextView textViewAirQuality;

    @BindView(R.id.textview_motion)
    public TextView textViewMotion;

    @BindView(R.id.textview_temperature_unit)
    public TextView textViewTemperatureUnit;

    @BindView(R.id.textview_pressure_unit)
    public TextView textViewPressureUnit;

    @BindView(R.id.textview_motion_unit)
    public TextView textViewMotionUnit;

    public ScreensaverFragment() {
        dateTimeZone = DateTimeZone.forID(Default.TIMEZONE);
        formatClock = ClockMode._24H;
        formatDate = FormatDate.EEEE_DD_MM_YYYY;
        formatTime = FormatTime.HH_MM;
        unitTemperature = UnitTemperature.CELSIUS;
        unitPressure = UnitPressure.PASCAL;

        presenter = new ScreenSaverPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screensaver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.observeDateChanged(view.getContext());
        data();
    }

    @Override
    public void onResume() {
        Objects.requireNonNull(getView()).setKeepScreenOn(true);

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        Objects.requireNonNull(getView()).setKeepScreenOn(false);
    }

    @Override
    public void showLoading(boolean isLoading) {
        //NO-OP
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onTemperatureChanged(final float value) {
        textViewTemperature.setText(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))));
    }

    @Override
    public void onPressureChanged(final float value) {
        textViewPressure.setText(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, value))));
    }

    @Override
    public void onHumidityChanged(final float value) {
        textViewHumidity.setText(String.valueOf(MathKit.round(value)));
    }

    @Override
    public void onAirQualityChanged(float value) {
        final Pair<String, Integer> pair = MathKit.convertIAQValueToLabelAndColor(value);

        textViewAirQuality.setText(pair.first);
    }

    @Override
    public void onAccelerationChanged(float[] values) {
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
        textViewMotion.setText(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, value))));
    }

    @Override
    public void onDateChanged() {
        setDate();
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

        ((ThermopileApplication) requireActivity().getApplication()).refresh();
    }

    private void setDate() {
        final DateTime dateTime = new DateTime(dateTimeZone);

        if (formatDate.contains(FormatDay.EEEE)) {
            textViewDate.setText(dateTime.toString(formatDate.replace(FormatDay.EEEE, "").trim()));
            textViewDay.setText(dateTime.toString(FormatDay.EEEE));
        } else if (formatDate.contains(FormatDay.EE)) {
            textViewDate.setText(dateTime.toString(formatDate.replace(FormatDay.EE, "").trim()));
            textViewDay.setText(dateTime.toString(FormatDay.EE));
        } else {
            textViewDate.setText(dateTime.toString(formatDate));
            textViewDay.setVisibility(View.INVISIBLE);
        }
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
            default:
                textViewMotionUnit.setText(getString(R.string.unit_acceleration_ms2));
                break;
        }
    }
}

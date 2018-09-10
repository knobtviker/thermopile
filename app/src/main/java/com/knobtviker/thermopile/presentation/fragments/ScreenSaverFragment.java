package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.MeasuredData;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScreenSaverFragment extends BaseFragment<ScreenSaverContract.Presenter> implements ScreenSaverContract.View {

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

    @BindView(R.id.textview_humidity_unit)
    public TextView textViewHumidityUnit;

    @BindView(R.id.textview_motion_unit)
    public TextView textViewMotionUnit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screensaver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void load() {
        presenter.observeDateChanged(requireContext());
        presenter.observeAtmosphere();
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
    public void onDateChanged(@NonNull String date, @NonNull String day) {
        textViewDate.setText(date);
        if (TextUtils.isEmpty(day)) {
            textViewDay.setVisibility(View.GONE);
        } else {
            textViewDay.setVisibility(View.VISIBLE);
        }
        textViewDay.setText(day);
    }

    @Override
    public void onZoneAndFormatChanged(@NonNull String timezone, boolean is24HourMode, @NonNull String formatTime) {
        textViewClock.setTimeZone(timezone);
        if (is24HourMode) {
            textViewClock.setFormat24Hour(formatTime);
            textViewClock.setFormat12Hour(null);
        } else {
            textViewClock.setFormat12Hour(formatTime);
            textViewClock.setFormat24Hour(null);
        }
    }

    @Override
    public void onTemperatureUnitChanged(@StringRes int resId) {
        textViewTemperatureUnit.setText(getString(resId));
    }

    @Override
    public void onPressureUnitChanged(int resId) {
        textViewPressureUnit.setText(getString(resId));
    }

    @Override
    public void ontHumidityUnitChanged(int resId) {
        textViewHumidityUnit.setText(getString(resId));
    }

    @Override
    public void onMotionUnitChanged(int resId) {
        textViewMotionUnit.setText(getString(resId));
    }

    @Override
    public void onTemperatureChanged(@NonNull MeasuredData measuredData) {
        textViewTemperature.setText(measuredData.readableValue());
    }

    @Override
    public void onHumidityChanged(@NonNull MeasuredData measuredData) {
        textViewHumidity.setText(measuredData.readableValue());
    }

    @Override
    public void onPressureChanged(@NonNull MeasuredData measuredData) {
        textViewPressure.setText(measuredData.readableValue());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAirQualityChanged(@NonNull MeasuredData measuredData) {
        textViewAirQuality.setText(measuredData.readableValue());
    }

    @Override
    public void onAccelerationChanged(@NonNull MeasuredData measuredData) {
        textViewMotion.setText(measuredData.readableValue());
    }
}

package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Reading;
import com.knobtviker.thermopile.data.models.presentation.Settings;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ScreenSaverPresenter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScreensaverFragment extends BaseFragment<ScreenSaverContract.Presenter> implements ScreenSaverContract.View {
    public static final String TAG = ScreensaverFragment.class.getSimpleName();

    private Settings settings = Settings.EMPTY();

    @BindView(R.id.textview_clock)
    public TextView textViewClock;

    @BindView(R.id.textview_date)
    public TextView textViewDate;

    @BindView(R.id.textview_day)
    public TextView textViewDay;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.textview_humidity)
    public TextView textViewHumidity;

    @BindView(R.id.textview_pressure)
    public TextView textViewPressure;

    public static Fragment newInstance() {
        return new ScreensaverFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);

        presenter = new ScreenSaverPresenter(this.getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_screensaver, container, false);

        bind(this, view);

        settings();
        onClockTick();
        startClock();

        return view;
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO: Whaat
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onClockTick() {
        final DateTime dateTime = new DateTime(DateTimeZone.forID("Europe/Zagreb"));
        setDateTime(dateTime);

        data();
    }

    @Override
    public void onData(@NonNull Reading data) {
        populateData(data);
    }

    @Override
    public void onSettings(@NonNull Settings settings) {
        this.settings = settings;

        Log.i(TAG, settings.toString());
    }

    private void setDateTime(@NonNull final DateTime dateTime) {
        setTime(dateTime.toString("HH:mm"));
        setDate(dateTime.toString("dd.MM.yyyy."));
        setDay(dateTime.toString("EEEE"));
    }

    private void setTime(@NonNull final String time) {
        textViewClock.setText(time);
    }

    private void setDate(@NonNull final String date) {
        textViewDate.setText(date);
    }

    private void setDay(@NonNull final String date) {
        textViewDay.setText(date);
    }

    private void startClock() {
        presenter.startClock();
    }

    private void settings() {
        presenter.settings();
    }

    private void data() {
        presenter.data();
    }

    private void populateData(@NonNull final Reading data) {
        textViewTemperature.setText(round(data.temperature(), 1).toString());
        textViewHumidity.setText(round(data.humidity(), 1).toString());
        textViewPressure.setText(round(data.pressure(), 1).toString());
    }

    private BigDecimal round(final float d, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}

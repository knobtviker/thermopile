package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ScreenSaverPresenter;
import com.knobtviker.thermopile.presentation.utils.MathKit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScreensaverFragment extends BaseFragment<ScreenSaverContract.Presenter> implements ScreenSaverContract.View {
    public static final String TAG = ScreensaverFragment.class.getSimpleName();

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

        presenter = new ScreenSaverPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_screensaver, container, false);

        bind(this, view);

        presenter.startClock();
        presenter.data();
        presenter.settings();

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
        //TODO: Move and get this timezone from Settings in Realm
        setDateTime(new DateTime(DateTimeZone.forID("Europe/Zagreb")));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDataChanged(@NonNull Atmosphere data) {
        //TODO: Change units and recalculate Atmosphere data according to Settings loaded
        textViewTemperature.setText(MathKit.round(data.temperature(), 0).toString());
        textViewHumidity.setText(MathKit.round(data.humidity(), 0).toString());
        textViewPressure.setText(MathKit.round(data.pressure(), 0).toString());
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        //TODO: Change units and recalculate Atmosphere data
        Log.i(TAG, settings.toString());
    }

    private void setDateTime(@NonNull final DateTime dateTime) {
        //TODO: Move and get these from Settings in Realm
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
}

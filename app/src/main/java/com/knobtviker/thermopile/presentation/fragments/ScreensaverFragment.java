package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
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
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ScreenSaverPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.MathKit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScreensaverFragment extends BaseFragment<ScreenSaverContract.Presenter> implements ScreenSaverContract.View {
    public static final String TAG = ScreensaverFragment.class.getSimpleName();

    private DateTimeZone dateTimeZone;
    private int formatClock;
    private String formatDate;
    private String formatTime;

    @BindView(R.id.textview_clock)
    public TextView textViewClock;

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

    public static Fragment newInstance() {
        return new ScreensaverFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);

        dateTimeZone = DateTimeZone.forID(Constants.DEFAULT_TIMEZONE);
        formatClock = Constants.CLOCK_MODE_24H;
        formatDate = Constants.DEFAULT_FORMAT_DATE;
        formatTime = Constants.FORMAT_TIME_LONG_24H;

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
        final DateTime dateTime = new DateTime(dateTimeZone);
        setDateTime(dateTime);
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
        dateTimeZone = DateTimeZone.forID(settings.timezone());
        formatClock = settings.formatClock();
        formatDate = settings.formatDate();
        formatTime = settings.formatTime();
    }

    private void setDateTime(@NonNull final DateTime dateTime) {
        if (formatClock == Constants.CLOCK_MODE_12H) {
            if (formatTime.contains(Constants.FORMAT_TIME_LONG_24H)) {
                formatTime = formatTime.replace(Constants.FORMAT_TIME_LONG_24H, Constants.FORMAT_TIME_LONG_12H);
            } else if (formatTime.contains(Constants.FORMAT_TIME_SHORT_24H)) {
                formatTime = formatTime.replace(Constants.FORMAT_TIME_SHORT_24H, Constants.FORMAT_TIME_SHORT_12H);
            }
        }
        setTime(dateTime.toString(formatTime));

        if (formatDate.contains(Constants.FORMAT_DAY_LONG)) {
            setDate(dateTime.toString(formatDate.replace(Constants.FORMAT_DAY_LONG, "").trim()));
            setDay(dateTime.toString(Constants.FORMAT_DAY_LONG));
        } else if (formatDate.contains(Constants.FORMAT_DAY_SHORT)) {
            setDate(dateTime.toString(formatDate.replace(Constants.FORMAT_DAY_SHORT, "").trim()));
            setDay(dateTime.toString(Constants.FORMAT_DAY_SHORT));
        } else {
            setDate(dateTime.toString(formatDate));
            textViewDay.setVisibility(View.INVISIBLE);
        }
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

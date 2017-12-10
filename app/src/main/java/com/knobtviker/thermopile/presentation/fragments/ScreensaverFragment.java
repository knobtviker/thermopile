package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
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

    private IntentFilter intentFilter;

    private BroadcastReceiver dateChangedReceiver;

    private DateTimeZone dateTimeZone;
    private int formatClock;
    private String formatDate;
    private String formatTime;
    private int unitTemperature;
    private int unitPressure;

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

    @BindView(R.id.textview_temperature_unit)
    public TextView textViewTemperatureUnit;

    @BindView(R.id.textview_pressure_unit)
    public TextView textViewPressureUnit;

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
        unitTemperature = Constants.UNIT_TEMPERATURE_CELSIUS;
        unitPressure = Constants.UNIT_PRESSURE_PASCAL;

        presenter = new ScreenSaverPresenter(this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);

        dateChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {

                    setDate();
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_screensaver, container, false);

        bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(dateChangedReceiver, intentFilter);

        data();

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(dateChangedReceiver);
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO: Whaat
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDataChanged(@NonNull Atmosphere data) {
        textViewTemperature.setText(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, data.temperature()), 0).toString());
        textViewHumidity.setText(MathKit.round(data.humidity(), 0).toString());
        textViewPressure.setText(MathKit.round(MathKit.applyPressureUnit(unitPressure, data.pressure()), 0).toString());
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        dateTimeZone = DateTimeZone.forID(settings.timezone());
        formatClock = settings.formatClock();
        formatDate = settings.formatDate();
        formatTime = settings.formatTime();
        unitTemperature = settings.unitTemperature();
        unitPressure = settings.unitPressure();

        setFormatClock();
        setTemperatureUnit();
        setPressureUnit();
        setDate();
    }

    private void setDate() {
        final DateTime dateTime = new DateTime(dateTimeZone);

        if (formatDate.contains(Constants.FORMAT_DAY_LONG)) {
            textViewDate.setText(dateTime.toString(formatDate.replace(Constants.FORMAT_DAY_LONG, "").trim()));
            textViewDay.setText(dateTime.toString(Constants.FORMAT_DAY_LONG));
        } else if (formatDate.contains(Constants.FORMAT_DAY_SHORT)) {
            textViewDate.setText(dateTime.toString(formatDate.replace(Constants.FORMAT_DAY_SHORT, "").trim()));
            textViewDay.setText(dateTime.toString(Constants.FORMAT_DAY_SHORT));
        } else {
            textViewDate.setText(dateTime.toString(formatDate));
            textViewDay.setVisibility(View.INVISIBLE);
        }
    }

    private void data() {
        presenter.data(realm);
        presenter.settings(realm);
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
}

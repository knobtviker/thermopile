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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.data.sources.raw.RelayRawDataSource;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.views.ArcView;
import com.knobtviker.thermopile.presentation.views.adapters.HoursAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends BaseFragment<MainContract.Presenter> implements MainContract.View {
    public static final String TAG = MainFragment.class.getSimpleName();

    private IntentFilter intentFilter;

    private BroadcastReceiver dateChangedReceiver;

    private MainCommunicator mainCommunicator;

    private HoursAdapter hoursAdapter;

    private ImmutableList<Threshold> thresholdsToday = ImmutableList.of();

    private DateTimeZone dateTimeZone;
    private int formatClock;
    private String formatDate;
    private String formatTime;
    private int unitTemperature;
    private int unitPressure;

    @BindView(R.id.textview_date)
    public TextView textViewDate;

    @BindView(R.id.textview_clock)
    public TextClock textViewClock;

    @BindView(R.id.textview_humidity)
    public TextView textViewHumidity;

    @BindView(R.id.textview_pressure)
    public TextView textViewPressure;

    @BindView(R.id.textview_iaq)
    public TextView textViewIAQ;

    @BindView(R.id.arc_temperature)
    public ArcView arcViewTemperature;

    @BindView(R.id.arc_humidity)
    public ArcView arcViewHumidity;

    @BindView(R.id.arc_iaq)
    public ArcView arcViewIAQ;

    @BindView(R.id.arc_pressure)
    public ArcView arcViewPressure;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.textview_temperature_unit)
    public TextView textViewTemperatureUnit;

    @BindView(R.id.textview_pressure_unit)
    public TextView textViewPressureUnit;

    @BindView(R.id.recyclerview_hours)
    public RecyclerView recyclerView;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainCommunicator) {
            mainCommunicator = (MainCommunicator) context;
        }
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

        presenter = new MainPresenter(this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);

        dateChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {

                    setDate();
                    thresholds();
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        bind(this, view);

        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(dateChangedReceiver, intentFilter);

        data();
        thresholds();

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(dateChangedReceiver);
    }

    @Override
    public void onDetach() {

        mainCommunicator = null;

        super.onDetach();
    }

    @Override
    public void showLoading(boolean isLoading) {
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
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

    @Override
    public void onThresholdsChanged(@NonNull RealmResults<Threshold> thresholds) {
        Log.i(TAG, thresholds.size() + "");
//        hoursAdapter.applyThreasholds(thresholds); //TODO: Fix this bad logic
    }

    @OnClick({R.id.floatingactionbutton_down, R.id.floatingactionbutton_up, R.id.button_charts, R.id.button_schedule, R.id.button_settings})
    public void onActionDownClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.floatingactionbutton_down:
                RelayRawDataSource.getInstance()
                    .on()
                    .subscribe();
                break;
            case R.id.floatingactionbutton_up:
                RelayRawDataSource.getInstance()
                    .off()
                    .subscribe();
                break;
            case R.id.button_charts:
                //TBD...
                break;
            case R.id.button_schedule:
                mainCommunicator.showSchedule();
                break;
            case R.id.button_settings:
                mainCommunicator.showSettings();
                break;
        }
    }

    public void setAtmosphere(@NonNull final Atmosphere atmosphere) {
        onTemperatureChanged(atmosphere.temperature());
        onHumidityChanged(atmosphere.humidity());
        onPressureChanged(atmosphere.pressure());
        onAirQualityChanged(atmosphere.airQuality());
        onMotionChanged(atmosphere.acceleration(), atmosphere.angularVelocity(), atmosphere.magneticField());
    }

    public void moveHourLine(@NonNull final DateTime dateTime) {
        final int currentHour = dateTime.getHourOfDay();
        if (currentHour > 6 && currentHour < 17) {
            recyclerView.scrollToPosition(currentHour - 6);
        } else if (currentHour <= 6) {
            recyclerView.scrollToPosition(0);
        } else if (currentHour >= 17) {
            recyclerView.scrollToPosition(12);
        }
    }

    private void setupRecyclerView() {
        hoursAdapter = new HoursAdapter(this.getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(hoursAdapter);

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressLint("SetTextI18n")
    private void onTemperatureChanged(final float value) {
        arcViewTemperature.setProgress(value / Constants.MEASURED_TEMPERATURE_MAX);
        textViewTemperature.setText(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))));
    }

    @SuppressLint("SetTextI18n")
    private void onHumidityChanged(final float value) {
        arcViewHumidity.setProgress(value / Constants.MEASURED_HUMIDITY_MAX);
        textViewHumidity.setText(String.valueOf(MathKit.round(value)));
    }

    @SuppressLint("SetTextI18n")
    private void onPressureChanged(final float value) {
        arcViewPressure.setProgress(value / Constants.MEASURED_PRESSURE_MAX);
        textViewPressure.setText(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, value))));
    }

    private void onAirQualityChanged(final float value) {
        final Pair<String, Integer> pair = convertIAQValueToLabelAndColor(value);

        textViewIAQ.setText(pair.first);
        arcViewIAQ.setProgressColor(pair.second);
        arcViewIAQ.setProgress((500.0f - value) / 500.0f);
    }

    // Calculate pitch, roll, and heading.
    // Pitch/roll calculations take from this app note:
    // http://cache.freescale.com/files/sensors/doc/app_note/AN3461.pdf?fpsp=1
    // Heading calculations taken from this app note:
    // http://www51.honeywell.com/aero/common/documents/myaerospacecatalog-documents/Defense_Brochures-documents/Magnetic__Literature_Application_notes-documents/AN203_Compass_Heading_Using_Magnetometers.pdf
    // The LSM9DS1's mag x and y axes are opposite to the accelerometer,
    // so my, mx are substituted for each other.
    // Pay attention what the position of the sensor is ato its hardcoded axes, hence the index shuffle.
    private void onMotionChanged(final float[] acceleration, final float[] angularVelocity, final float[] magneticField) {
        final double ax = acceleration[0];
        final double ay = acceleration[2];
        final double az = acceleration[1];

        final double mx = -magneticField[2];
        final double my = -magneticField[0];
        final double mz = magneticField[1];

        double roll = Math.atan2(ay, az);

        double pitch = Math.atan2(-ax, Math.sqrt(ay * ay + az * az));

        double heading;
        if (my == 0) {
            heading = (mx < 0) ? Math.PI : 0;
        } else {
            heading = Math.atan2(mx, my);
        }

        heading -= Constants.DECLINATION * Math.PI / 180;

        if (heading > Math.PI) {
            heading -= (2 * Math.PI);
        } else if (heading < -Math.PI) {
            heading += (2 * Math.PI);
        } else if (heading < 0) {
            heading += 2 * Math.PI;
        }

        // Convert everything from radians to degrees:
        heading *= 180.0 / Math.PI;
        pitch *= 180.0 / Math.PI;
        roll *= 180.0 / Math.PI;

        final double accelerationTotal = Math.sqrt(ax * ax + ay * ay * az * az);

        Log.i(TAG, "Acceleration: " + accelerationTotal + " --- Roll: " + roll + " --- Pitch: " + pitch + " --- Heading: " + heading);
        //TODO: Now figure out how to draw roll, pitch and heading. Use aero gauges and compass UI.
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

    private void setDate() {
        final DateTime dateTime = new DateTime(dateTimeZone);
        textViewDate.setText(dateTime.toString(formatDate));
    }

    private void data() {
        presenter.settings(realm);
    }

    private void thresholds() {
        presenter.thresholdsForToday(realm, DateTime.now().dayOfWeek().get());
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
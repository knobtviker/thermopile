package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Reading;
import com.knobtviker.thermopile.data.models.presentation.Settings;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.MiniPID;
import com.knobtviker.thermopile.presentation.views.CircularSeekBar;
import com.knobtviker.thermopile.presentation.views.adapters.HoursAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends BaseFragment<MainContract.Presenter> implements MainContract.View {
    public static final String TAG = MainFragment.class.getSimpleName();

    private MainCommunicator mainCommunicator;

    private HoursAdapter hoursAdapter;

    private Settings settings = Settings.EMPTY();

    private ImmutableList<Threshold> thresholdsToday = ImmutableList.of();

    private MiniPID miniPID;
//    private float fakeIncrease = 0.05f;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.textview_date)
    public TextView textViewDate;

    @BindView(R.id.textview_clock)
    public TextView textViewClock;

    @BindView(R.id.textview_humidity)
    public TextView textViewHumidity;

    @BindView(R.id.textview_pressure)
    public TextView textViewPressure;

    @BindView(R.id.seekbar_temperature)
    public CircularSeekBar seekBarTemperature;

    @BindView(R.id.textview_current_temperature)
    public TextView textViewCurrentTemperature;

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

        setHasOptionsMenu(true);

        presenter = new MainPresenter(this.getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        bind(this, view);

        setupPID();
        setupToolbar();
        setupSeekBar();
        onClockTick();
        setupRecyclerView();
        startClock();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_schedule) {
            mainCommunicator.showSchedule();
        } else if (item.getItemId() == R.id.action_settings) {
            mainCommunicator.showSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.thresholdsForToday(DateTime.now().dayOfWeek().get());
        presenter.settings();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mainCommunicator = null;
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO:
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
    public void onThresholds(@NonNull ImmutableList<Threshold> thresholds) {
        this.thresholdsToday = thresholds;

        hoursAdapter.applyThreasholds(thresholds);
    }

    @Override
    public void onSettings(@NonNull Settings settings) {
        this.settings = settings;

        Log.i(TAG, settings.toString());
    }

    @OnClick(R.id.floatingactionbutton_down)
    public void onActionDownClicked() {
        if (seekBarTemperature.getProgress() > seekBarTemperature.getMin()) {
            seekBarTemperature.setProgress(seekBarTemperature.getProgress() - 1);
        }
    }

    @OnClick(R.id.floatingactionbutton_up)
    public void onActionUpClicked() {
        if (seekBarTemperature.getProgress() < seekBarTemperature.getMax()) {
            seekBarTemperature.setProgress(seekBarTemperature.getProgress() + 1);
        }
    }

    //TODO: this needs more calibration to work properly but it does behave promising...
    private void setupPID() {
        miniPID = new MiniPID(1.0, 0.5, 0.0);
        miniPID.setOutputLimits(5, 35);
        //miniPID.setMaxIOutput(2);
        miniPID.setOutputRampRate(3);
        //miniPID.setOutputFilter(.3);
//        miniPID.setSetpointRange(5);
    }

    private void setupToolbar() {
        setupCustomActionBar(toolbar);
    }

    private void setupSeekBar() {
        seekBarTemperature.setProgressString(round((Constants.MEASURED_TEMPERATURE_MAX - Constants.MEASURED_TEMPERATURE_MIN) * (seekBarTemperature.getProgress() / 100.0f) + Constants.MEASURED_TEMPERATURE_MIN, 1).toString());
        seekBarTemperature.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                seekBarTemperature.setProgressString(round((Constants.MEASURED_TEMPERATURE_MAX - Constants.MEASURED_TEMPERATURE_MIN) * (seekBarTemperature.getProgress() / 100.0f) + Constants.MEASURED_TEMPERATURE_MIN, 1).toString());
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
    }

    private void setupRecyclerView() {
        hoursAdapter = new HoursAdapter(this.getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(hoursAdapter);

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void setDateTime(@NonNull final DateTime dateTime) {
        setDate(dateTime.toString("EEEE dd.MM.yyyy."));
        setTime(dateTime.toString(String.format("HH%smm", dateTime.getSecondOfMinute() % 2 == 0 ? ":" : " ")));
    }

    private void setDate(@NonNull final String date) {
        textViewDate.setText(date);
    }

    private void setTime(@NonNull final String time) {
        textViewClock.setText(time);
    }

    private void startClock() {
        presenter.startClock();
    }

    private void data() {
        presenter.data();
    }

    @SuppressLint("SetTextI18n")
    private void populateData(@NonNull final Reading data) {
        //TODO: Apply data units and formats and timezone
        //TODO: Find the right threshold if todays list is not empty.
        //TODO: If threshold is found set it as progress on seekbar and/or use as target value
        //TODO: Else set seekbar progress to be the current measured temperature data. Do not start PID.

        textViewCurrentTemperature.setText(round(data.temperature(), 1).toString());
        textViewHumidity.setText(round(data.humidity(), 1).toString());
        textViewPressure.setText(round(data.pressure(), 1).toString());

//        final double target = (Constants.MEASURED_TEMPERATURE_MAX - Constants.MEASURED_TEMPERATURE_MIN) * (seekBarTemperature.getProgress() / 100.0f) + Constants.MEASURED_TEMPERATURE_MIN;
//        final double measured = data.temperature() + fakeIncrease;
//        if (measured < target) {
//            fakeIncrease = fakeIncrease + 0.05f;
//            final double output = miniPID.getOutput(measured, target);
//            Log.i(TAG, String.format("Measured: %3.2f , Target: %3.2f , Error: %3.2f , Output: %3.2f\n", measured, target, (target - measured), output));
//        }
    }

    private BigDecimal round(final float d, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}
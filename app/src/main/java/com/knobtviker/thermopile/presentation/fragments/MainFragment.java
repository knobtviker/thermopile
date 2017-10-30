package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.raw.RelayRawDataSource;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.MiniPID;
import com.knobtviker.thermopile.presentation.views.CircularSeekBar;
import com.knobtviker.thermopile.presentation.views.adapters.HoursAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends BaseFragment<MainContract.Presenter> implements MainContract.View {
    public static final String TAG = MainFragment.class.getSimpleName();

    private MainCommunicator mainCommunicator;

    private HoursAdapter hoursAdapter;

    private ImmutableList<Threshold> thresholdsToday = ImmutableList.of();

    private MiniPID miniPID;
//    private float fakeIncrease = 0.05f;

    private DateTimeZone dateTimeZone;

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

    @BindView(R.id.textview_temperature)
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

        dateTimeZone = DateTimeZone.forID("Europe/Zagreb");

        presenter = new MainPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        bind(this, view);

        presenter.startClock();
        presenter.data();
        presenter.settings();
        presenter.thresholdsForToday(DateTime.now().dayOfWeek().get());

        setupPID();
        setupToolbar();
        setupSeekBar();
        setupRecyclerView();

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
    public void onClockTick() {
        //TODO: Move and get this timezone from Settings in Realm
        final DateTime dateTime = new DateTime(dateTimeZone);
        setDateTime(dateTime);
        maybeApplyThresholds(dateTime);
        moveHourLine(dateTime);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDataChanged(@NonNull Atmosphere data) {
        //TODO: Change units and recalculate Atmosphere data according to Settings loaded
        //TODO: Apply data units and formats and timezone

        //TODO: Find the right threshold if todays list is not empty.
        final Optional<Threshold> thresholdOptional = thresholdsToday.stream()
            .filter(threshold -> {
                final DateTime now = DateTime.now();
                return threshold.startHour() < now.getHourOfDay() && threshold.startMinute() < now.getMinuteOfHour()
                    && now.getHourOfDay() <= threshold.endHour() && now.getMinuteOfHour() <= threshold.endMinute();
            })
            .findFirst();
        if (thresholdOptional.isPresent()) {
            //TODO: If threshold is found set it as progress on seekbar and/or use as target value
            seekBarTemperature.setProgress(thresholdOptional.get().temperature());
        } else {
            //TODO: Else set seekbar progress to be the current measured temperature data. Do not start PID.
            seekBarTemperature.setProgress(Math.round(data.temperature()));
        }

        textViewCurrentTemperature.setText(MathKit.round(data.temperature(), 0).toString());
        textViewHumidity.setText(MathKit.round(data.humidity(), 0).toString());
        textViewPressure.setText(MathKit.round(data.pressure(), 0).toString());

//        final double target = (Constants.MEASURED_TEMPERATURE_MAX - Constants.MEASURED_TEMPERATURE_MIN) * (seekBarTemperature.getProgress() / 100.0f) + Constants.MEASURED_TEMPERATURE_MIN;
//        final double measured = data.temperature() + fakeIncrease;
//        if (measured < target) {
//            fakeIncrease = fakeIncrease + 0.05f;
//            final double output = miniPID.getOutput(measured, target);
//            Log.i(TAG, String.format("Measured: %3.2f , Target: %3.2f , Error: %3.2f , Output: %3.2f\n", measured, target, (target - measured), output));
//        }
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        //TODO: Change units and recalculate Atmosphere data
        dateTimeZone = DateTimeZone.forID(settings.timezone());
        Log.i(TAG, settings.toString());
    }

    @Override
    public void onThresholdsChanged(@NonNull RealmResults<Threshold> thresholds) {
//        hoursAdapter.applyThreasholds(thresholds); //TODO: Fix this bad logic
    }

    @OnClick(R.id.floatingactionbutton_down)
    public void onActionDownClicked() {
        if (seekBarTemperature.getProgress() > seekBarTemperature.getMin()) {
            seekBarTemperature.setProgress(seekBarTemperature.getProgress() - 1);
        }
        RelayRawDataSource.getInstance()
            .on()
            .subscribeOn(SchedulerProvider.getInstance().ui())
            .observeOn(SchedulerProvider.getInstance().ui())
            .subscribe();
    }

    @OnClick(R.id.floatingactionbutton_up)
    public void onActionUpClicked() {
        if (seekBarTemperature.getProgress() < seekBarTemperature.getMax()) {
            seekBarTemperature.setProgress(seekBarTemperature.getProgress() + 1);
        }
        RelayRawDataSource.getInstance()
            .off()
            .subscribeOn(SchedulerProvider.getInstance().ui())
            .observeOn(SchedulerProvider.getInstance().ui())
            .subscribe();
    }

    public void setDateTime(@NonNull final DateTime dateTime) {
        setDate(dateTime.toString("EEEE dd.MM.yyyy."));
        setTime(dateTime.toString("HH:mm"));
    }

    public void maybeApplyThresholds(@NonNull final DateTime dateTime) {
        if (dateTime.getSecondOfDay() == 0) {
            presenter.thresholdsForToday(DateTime.now().dayOfWeek().get());
        }
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
        seekBarTemperature.setProgressString(MathKit.round((Constants.MEASURED_TEMPERATURE_MAX - Constants.MEASURED_TEMPERATURE_MIN) * (seekBarTemperature.getProgress() / 100.0f) + Constants.MEASURED_TEMPERATURE_MIN, 1).toString());
        seekBarTemperature.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                seekBarTemperature.setProgressString(MathKit.round((Constants.MEASURED_TEMPERATURE_MAX - Constants.MEASURED_TEMPERATURE_MIN) * (seekBarTemperature.getProgress() / 100.0f) + Constants.MEASURED_TEMPERATURE_MIN, 1).toString());
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

    private void setDate(@NonNull final String date) {
        textViewDate.setText(date);
    }

    private void setTime(@NonNull final String time) {
        textViewClock.setText(time);
    }
}
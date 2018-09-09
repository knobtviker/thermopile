package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.MeasuredData;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.utils.controllers.PIDController;
import com.knobtviker.thermopile.presentation.views.ArcView;
import com.knobtviker.thermopile.presentation.views.adapters.ThresholdAdapter;
import com.knobtviker.thermopile.presentation.views.listeners.DayScrollListener;

import java.util.List;

import javax.inject.Inject;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends BaseFragment<MainContract.Presenter> implements MainContract.View, DayScrollListener.Listener {

    @Inject
    ThresholdAdapter thresholdAdapter;

    @Inject
    PIDController pidController;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

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

    @BindView(R.id.textview_humidity_unit)
    public TextView textViewHumidityUnit;

    @BindView(R.id.textview_motion_unit)
    public TextView textViewMotionUnit;

    @BindView(R.id.textview_day)
    public TextView textViewDay;

    @BindView(R.id.recyclerview_thresholds)
    public RecyclerView recyclerViewThresholds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
    }

    @Override
    protected void load() {
        presenter.observeDateChanged(requireContext());
        presenter.observeAtmosphere();
    }

    @Override
    public void showLoading(boolean isLoading) {
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onDateChanged(@NonNull String date) {
        textViewDate.setText(date);
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
    public void onThresholdUnitAndFormatChanged(int unitTemperature, @NonNull String formatTime, @NonNull String formatDate) {
//        thresholdAdapter.setUnitAndFormat(unitTemperature, formatTime, formatDate); //TODO: Fix this crash

        if (recyclerViewThresholds.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewThresholds.getLayoutManager();
            if (thresholdAdapter.getItemCount() > 0 && linearLayoutManager.findFirstVisibleItemPosition() != -1) {
                textViewDay.setText(thresholdAdapter.getItemDay(linearLayoutManager.findFirstVisibleItemPosition()));
            }
        }
    }

    @Override
    public void onTemperatureChanged(@NonNull MeasuredData measuredData) {
        arcViewTemperature.setProgress(measuredData.progress());
        textViewTemperature.setText(measuredData.readableValue());

        //        pidController.doPID(Math.round(value));
    }

    @Override
    public void onHumidityChanged(@NonNull MeasuredData measuredData) {
        arcViewHumidity.setProgress(measuredData.progress());
        textViewHumidity.setText(measuredData.readableValue());
    }

    @Override
    public void onPressureChanged(@NonNull MeasuredData measuredData) {
        arcViewPressure.setProgress(measuredData.progress());
        textViewPressure.setText(measuredData.readableValue());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onAirQualityChanged(@NonNull MeasuredData measuredData) {
        arcViewIAQ.setProgress(measuredData.progress());
        arcViewIAQ.setProgressColor(measuredData.color());
        textViewIAQ.setText(measuredData.readableValue());
    }

    @Override
    public void onAccelerationChanged(@NonNull MeasuredData measuredData) {
        arcViewMotion.setProgress(measuredData.progress());
        textViewMotion.setText(measuredData.readableValue());
    }

    @Override
    public void onThresholdsChanged(@NonNull List<ThresholdInterval> thresholdIntervals) {
        thresholdAdapter.setData(thresholdIntervals);
    }

    @Override
    public void onDayChanged() {
        if (thresholdAdapter.getItemCount() > 0) {
            if (recyclerViewThresholds.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewThresholds.getLayoutManager();
//                textViewDay.setText(thresholdAdapter.getItemDay(linearLayoutManager.findFirstVisibleItemPosition())); //TODO: Fix this crash
            }
        }
    }

    @OnClick({R.id.button_decrease, R.id.button_increase})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_decrease:
                //                RelayRawDataSource.getInstance()
                //                    .on()
                //                    .subscribe();
                break;
            case R.id.button_increase:
                //                RelayRawDataSource.getInstance()
                //                    .off()
                //                    .subscribe();
                break;
        }
    }

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.main);

        toolbar.setOnMenuItemClickListener(menuItem -> {
            NavigationUI.onNavDestinationSelected(menuItem, NavHostFragment.findNavController(this));
            return false;
        });
    }

    private void setupRecyclerView() {
        recyclerViewThresholds.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewThresholds.setAdapter(thresholdAdapter);
        recyclerViewThresholds.addOnScrollListener(DayScrollListener.create(this));
    }
}
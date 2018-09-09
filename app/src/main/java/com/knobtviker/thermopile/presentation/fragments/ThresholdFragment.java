package com.knobtviker.thermopile.presentation.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.time.Days;
import com.knobtviker.thermopile.presentation.shared.constants.time.Hours;
import com.knobtviker.thermopile.presentation.shared.constants.time.Minutes;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.views.DiscreteSeekBar;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;
import com.knobtviker.thermopile.presentation.views.dividers.GridItemDecoration;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 28/10/2017.
 */

public class ThresholdFragment extends BaseFragment<ThresholdContract.Presenter> implements ThresholdContract.View {

    private TimePickerDialog timePickerDialogStart;

    private TimePickerDialog timePickerDialogEnd;

    private long thresholdId = Default.INVALID_ID;

    private int maxWidth = Default.INVALID_WIDTH;

    @Days
    private int day;

    @Minutes
    private int startMinute;

    @Hours
    private int startTimeHour;

    @Minutes
    private int startTimeMinute;

    @Hours
    private int endTimeHour;

    @Minutes
    private int endTimeMinute;

    @Inject
    ColorAdapter colorAdapter;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.textview_time_start)
    public TextView textViewTimeStart;

    @BindView(R.id.textview_time_end)
    public TextView textViewTimeEnd;

    @BindView(R.id.seekbar_temperature)
    public DiscreteSeekBar seekBarTemperature;

    @BindView(R.id.recyclerview_colors)
    public RecyclerView recyclerViewColors;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Optional<Bundle> argumentsOptional = Optional.ofNullable(getArguments());
        argumentsOptional.ifPresent(bundle -> {
            final ThresholdFragmentArgs arguments = ThresholdFragmentArgs.fromBundle(bundle);
            thresholdId = arguments.getThresholdId();
            maxWidth = arguments.getMaxWidth();
            day = arguments.getDay();
            startMinute = arguments.getStartMinute();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_threshold, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
    }

    @Override
    protected void load() {
        if (thresholdId != Default.INVALID_ID) {
            presenter.loadById(thresholdId);
        } else if (maxWidth != Default.INVALID_WIDTH) {
            presenter.createNew(day, startMinute, maxWidth);
        }
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        Snackbar.make(Objects.requireNonNull(getView()), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void updateDialogStartTime(int startHour, int startMinute) {
        timePickerDialogStart.updateTime(startHour, startMinute);
    }

    @Override
    public void onTemperatureUnitChanged(int resId, float minimum, float maximum) {
        seekBarTemperature.newBuilder()
            .min(minimum)
            .max(maximum)
            .unit(getString(resId))
            .build();
    }

    @Override
    public void onClockAndTimeFormatChanged(boolean is24hMode, @NonNull String formattedTimeNow) {
        textViewTimeStart.setText(formattedTimeNow);
        textViewTimeEnd.setText(formattedTimeNow);

        timePickerDialogStart = new TimePickerDialog(
            requireContext(),
            this::onStartTimeSet,
            DateTimeKit.now().getHour(),
            DateTimeKit.now().getMinute(),
            is24hMode
        );

        timePickerDialogEnd = new TimePickerDialog(
            requireContext(),
            this::onEndTimeSet,
            DateTimeKit.now().getHour(),
            DateTimeKit.now().getMinute(),
            is24hMode
        );
    }

    @Override
    public void onStartTimeChanged(@NonNull String formattedTime) {
        textViewTimeStart.setText(formattedTime);
    }

    @Override
    public void onEndTimeChanged(@NonNull String formattedTime) {
        textViewTimeEnd.setText(formattedTime);
    }

    @OnClick({R.id.layout_start_time, R.id.layout_end_time})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.layout_start_time:
                timePickerDialogStart.show();
                break;
            case R.id.layout_end_time:
                timePickerDialogEnd.show();
                break;
        }
    }

    @Override
    public void onThreshold(@NonNull Threshold threshold) {
        this.day = threshold.day;
        this.startTimeHour = threshold.startHour;
        this.startTimeMinute = threshold.startMinute;
        this.endTimeHour = threshold.endHour;
        this.endTimeMinute = threshold.endMinute;

        seekBarTemperature.setProgress(threshold.temperature);
        timePickerDialogStart.updateTime(threshold.startHour, threshold.startMinute);
        timePickerDialogEnd.updateTime(threshold.endHour, threshold.endMinute);

        setStartTime(day + 1, startTimeHour, startTimeMinute);

        setEndTime(day + 1, endTimeHour, endTimeMinute);

        colorAdapter.setSelectedColor(threshold.color);
    }

    @Override
    public void onSaved() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    private void setupToolbar() {
        final NavController navController = NavHostFragment.findNavController(this);
        toolbar.setNavigationIcon(R.drawable.ic_discard);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
        toolbar.inflateMenu(R.menu.threshold);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.saveAction:
                    save();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void setupRecyclerView() {
        final int spanCount = 7;

        recyclerViewColors.setHasFixedSize(true);
        recyclerViewColors.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        recyclerViewColors.setAdapter(colorAdapter);
        recyclerViewColors.addItemDecoration(GridItemDecoration.create(getResources().getDimensionPixelSize(R.dimen.margin_xsmall)));
    }

    private void save() {
        final Threshold threshold = new Threshold();
        threshold.day = day;
        threshold.startHour = startTimeHour;
        threshold.startMinute = startTimeMinute;
        threshold.endHour = endTimeHour;
        threshold.endMinute = endTimeMinute;
        threshold.temperature = Math.round(seekBarTemperature.getProgressPercent() * MeasuredTemperature.MAXIMUM);
        threshold.color = colorAdapter.getSelectedColor();

        presenter.save(threshold);
    }

    private void setStartTime(final int day, final int hour, final int minute) {
        presenter.setStartTime(day, hour, minute);
    }

    private void setEndTime(final int day, final int hour, final int minute) {
        presenter.setEndTime(day, hour, minute);
    }

    private void onStartTimeSet(@NonNull final TimePicker view, final int hourOfDay, final int minute) {
        startTimeHour = hourOfDay;
        startTimeMinute = minute;

        setStartTime(day + 1, startTimeHour, startTimeMinute);
    }

    private void onEndTimeSet(@NonNull final TimePicker view, final int hourOfDay, final int minute) {
        endTimeHour = hourOfDay;
        endTimeMinute = minute;

        setEndTime(day + 1, endTimeHour, endTimeMinute);
    }
}

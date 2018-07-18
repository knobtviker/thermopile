package com.knobtviker.thermopile.presentation.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ThresholdPresenter;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.utils.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.utils.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.utils.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.utils.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.utils.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.constants.time.Days;
import com.knobtviker.thermopile.presentation.utils.constants.time.Hours;
import com.knobtviker.thermopile.presentation.utils.constants.time.Minutes;
import com.knobtviker.thermopile.presentation.views.DiscreteSeekBar;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;
import com.knobtviker.thermopile.presentation.views.dividers.GridItemDecoration;

import org.joda.time.DateTime;

import java.util.Objects;
import java.util.Optional;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 28/10/2017.
 */

public class ThresholdFragment extends BaseFragment<ThresholdContract.Presenter> implements ThresholdContract.View {
    public static String TAG = ThresholdFragment.class.getSimpleName();

    private long thresholdId = Default.INVALID_ID;

    private int maxWidth = Default.INVALID_WIDTH;

    @ClockMode
    private int formatClock;

    @FormatTime
    private String formatTime;

    @UnitTemperature
    private int unitTemperature;

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

    private TimePickerDialog timePickerDialogStart;

    private TimePickerDialog timePickerDialogEnd;

    private ColorAdapter colorAdapter;

    @BindView(R.id.textview_time_start)
    public TextView textViewTimeStart;

    @BindView(R.id.textview_time_end)
    public TextView textViewTimeEnd;

    @BindView(R.id.seekbar_temperature)
    public DiscreteSeekBar seekBarTemperature;

    @BindView(R.id.recyclerview_colors)
    public RecyclerView recyclerViewColors;

    public ThresholdFragment() {
        formatClock = ClockMode._24H;
        formatTime = FormatTime.HH_MM;
        unitTemperature = UnitTemperature.CELSIUS;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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

        setupSeekBar();
        setupTimePickers();
        setupRecyclerView();

        presenter = new ThresholdPresenter(this);

        load();
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
    public void onSettingsChanged(@NonNull Settings settings) {
        this.formatClock = settings.formatClock;
        this.formatTime = settings.formatTime;
        this.unitTemperature = settings.unitTemperature;

        setupSeekBar();
        setupTimePickers();
    }

    @Override
    public void onThreshold(@NonNull Threshold threshold) {
        populate(threshold);
    }

    @Override
    public void onSaved() {
        back();
    }

    @OnClick({R.id.button_discard, R.id.button_save, R.id.layout_start_time, R.id.layout_end_time})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_discard:
                back();
                break;
            case R.id.button_save:
                save();
                break;
            case R.id.layout_start_time:
                showStartTimePicker();
                break;
            case R.id.layout_end_time:
                showEndTimePicker();
                break;
        }
    }

    private void setupSeekBar() {
        String unit;

        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                unit = getString(R.string.unit_temperature_celsius);
                break;
            case UnitTemperature.FAHRENHEIT:
                unit = getString(R.string.unit_temperature_fahrenheit);
                break;
            case UnitTemperature.KELVIN:
                unit = getString(R.string.unit_temperature_kelvin);
                break;
            default:
                unit = getString(R.string.unit_temperature_celsius);
                break;
        }

        seekBarTemperature.newBuilder()
            .min(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MINIMUM))
            .max(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MAXIMUM))
            .unit(unit)
            .build();
    }

    private void setupTimePickers() {
        final DateTime now = DateTime.now();

        textViewTimeStart.setText(now.toString(formatTime));
        textViewTimeEnd.setText(now.toString(formatTime));

        timePickerDialogStart = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, minute) -> {
                startTimeHour = hourOfDay;
                startTimeMinute = minute;

                setStartTime(day + 1, startTimeHour, startTimeMinute);
            },
            now.getHourOfDay(),
            now.getMinuteOfHour(),
            formatClock == ClockMode._24H
        );

        timePickerDialogEnd = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, minute) -> {
                endTimeHour = hourOfDay;
                endTimeMinute = minute;

                setEndTime(day + 1, endTimeHour, endTimeMinute);
            },
            now.getHourOfDay(),
            now.getMinuteOfHour(),
            formatClock == ClockMode._24H
        );
    }

    private void setupRecyclerView() {
        final int spanCount = 7;
        colorAdapter = new ColorAdapter(requireContext());

        recyclerViewColors.setHasFixedSize(true);
        recyclerViewColors.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));
        recyclerViewColors.setAdapter(colorAdapter);
        recyclerViewColors.addItemDecoration(GridItemDecoration.create(getResources().getDimensionPixelSize(R.dimen.margin_small)));
    }

    private void load() {
        presenter.settings();

        if (thresholdId != Default.INVALID_ID) {
            presenter.loadById(thresholdId);
        } else if (maxWidth != Default.INVALID_WIDTH) {
            populate(startMinute, maxWidth);
        }
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

    private void populate(@NonNull final Threshold threshold) {
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

    private void populate(final int startMinuteX, final int maxWidth) {
        final int startMinuteInADay = Math.round(startMinuteX * (1440 / (float) maxWidth));

        final int startHour = startMinuteInADay / 60;
        final int startMinute = startMinuteInADay - startHour * 60;

        timePickerDialogStart.updateTime(startHour, startMinute);

        setStartTime(day + 1, startHour, startMinute);

        setEndTime(day + 1, startHour, startMinute);
    }

    private void showStartTimePicker() {
        timePickerDialogStart.show();
    }

    private void showEndTimePicker() {
        timePickerDialogEnd.show();
    }

    private void back() {
        NavHostFragment.findNavController(this).popBackStack();
    }

    private void setStartTime(final int day, final int hour, final int minute) {
        textViewTimeStart.setText(
            new DateTime()
                .withDayOfWeek(day)
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .toString(formatTime)
        );
    }

    private void setEndTime(final int day, final int hour, final int minute) {
        textViewTimeEnd.setText(
            new DateTime()
                .withDayOfWeek(day)
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .toString(formatTime)
        );
    }
}

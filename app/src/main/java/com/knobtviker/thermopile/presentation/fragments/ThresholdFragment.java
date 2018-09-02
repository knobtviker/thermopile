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

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultClockMode;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMaxWidth;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultThreshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.time.Days;
import com.knobtviker.thermopile.presentation.shared.constants.time.Hours;
import com.knobtviker.thermopile.presentation.shared.constants.time.Minutes;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;
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

    private ColorAdapter colorAdapter;

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
    @DefaultThreshold
    long thresholdId;

    @Inject
    @DefaultMaxWidth
    int maxWidth;

    @Inject
    @DefaultClockMode
    @ClockMode
    int formatClock;

    @Inject
    @DefaultFormatTime
    @FormatTime
    String formatTime;

    @Inject
    @DefaultTemperature
    @UnitTemperature
    int unitTemperature;

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
        setupSeekBar();
        setupTimePickers();
        setupRecyclerView();
    }

    @Override
    protected void load() {
        presenter.settings();

        if (thresholdId != Default.INVALID_ID) {
            presenter.loadById(thresholdId);
        } else if (maxWidth != Default.INVALID_WIDTH) {
            populate(startMinute, maxWidth);
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
        NavHostFragment.findNavController(this).navigateUp();
    }

    @OnClick({R.id.layout_start_time, R.id.layout_end_time})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.layout_start_time:
                showStartTimePicker();
                break;
            case R.id.layout_end_time:
                showEndTimePicker();
                break;
        }
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
        textViewTimeStart.setText(
            DateTimeKit.format(DateTimeKit.now(), formatTime)
        );
        textViewTimeEnd.setText(
            DateTimeKit.format(DateTimeKit.now(), formatTime)
        );

        timePickerDialogStart = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, minute) -> {
                startTimeHour = hourOfDay;
                startTimeMinute = minute;

                setStartTime(day + 1, startTimeHour, startTimeMinute);
            },
            DateTimeKit.now().getHour(),
            DateTimeKit.now().getMinute(),
            formatClock == ClockMode._24H
        );

        timePickerDialogEnd = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, minute) -> {
                endTimeHour = hourOfDay;
                endTimeMinute = minute;

                setEndTime(day + 1, endTimeHour, endTimeMinute);
            },
            DateTimeKit.now().getHour(),
            DateTimeKit.now().getMinute(),
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

    private void setStartTime(final int day, final int hour, final int minute) {
        textViewTimeStart.setText(
            DateTimeKit.format(day, hour, minute, formatTime)
        );
    }

    private void setEndTime(final int day, final int hour, final int minute) {
        textViewTimeEnd.setText(
            DateTimeKit.format(day, hour, minute, formatTime)
        );
    }
}

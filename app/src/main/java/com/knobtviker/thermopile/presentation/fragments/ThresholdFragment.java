package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ThresholdPresenter;
import com.knobtviker.thermopile.presentation.views.DiscreteSeekBar;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;

import org.joda.time.DateTime;

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

    private int day = -1;

    private int startMinute = -1;

    private int maxWidth = -1;

    private long thresholdId = -1L;

    private int startTimeHour = -1;

    private int startTimeMinute = -1;

    private int endTimeHour = -1;

    private int endTimeMinute = -1;

    private GridTimePickerDialog gridtimeStart;

    private GridTimePickerDialog gridtimeEnd;

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
        presenter = new ThresholdPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        final Optional<Bundle> argumentsOptional = Optional.ofNullable(getArguments());
        argumentsOptional.ifPresent(bundle -> {
            final ThresholdFragmentArgs arguments = ThresholdFragmentArgs.fromBundle(bundle);
            day = arguments.getDay();
            startMinute = arguments.getStartMinute();
            maxWidth = arguments.getMaxWidth();
            thresholdId = (long)arguments.getThresholdId();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_threshold, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSeekBar();
        setupTimePickers();
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        if (thresholdId != -1L) {
            presenter.loadById(thresholdId);
        } else if (day != -1 && startMinute != -1 && maxWidth != -1) {
            populate(startMinute, maxWidth);
        } else {
            //TODO: Show some impossible error
        }
        super.onResume();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
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
        //TODO: Use newBuilder() to apply Settings, min, max and section count constants
    }

    private void setupTimePickers() {
        final DateTime now = DateTime.now();

        textViewTimeStart.setText(now.toString("HH:mm"));
        textViewTimeEnd.setText(now.toString("HH:mm"));

        gridtimeStart = new GridTimePickerDialog.Builder(
            (viewGroup, hourOfDay, minute) -> {
                startTimeHour = hourOfDay;
                startTimeMinute = minute;
                final String startTime = new DateTime()
                    .withDayOfWeek(day + 1)
                    .withHourOfDay(startTimeHour)
                    .withMinuteOfHour(startTimeMinute)
                    .toString("HH:mm");
                textViewTimeStart.setText(startTime);
            },
            now.hourOfDay().get(),
            now.minuteOfHour().get(),
            DateFormat.is24HourFormat(getContext())
        )
            .build();

        gridtimeEnd = new GridTimePickerDialog.Builder(
            (viewGroup, hourOfDay, minute) -> {
                endTimeHour = hourOfDay;
                endTimeMinute = minute;
                final String endTime = new DateTime()
                    .withDayOfWeek(day + 1)
                    .withHourOfDay(endTimeHour)
                    .withMinuteOfHour(endTimeMinute)
                    .toString("HH:mm");
                textViewTimeEnd.setText(endTime);
            },
            now.hourOfDay().get(),
            now.minuteOfHour().get(),
            DateFormat.is24HourFormat(getContext())
        )
            .build();
    }

    private void setupRecyclerView() {
        colorAdapter = new ColorAdapter(getContext());

        recyclerViewColors.setHasFixedSize(true);
        recyclerViewColors.setLayoutManager(new GridLayoutManager(getContext(), 7));
        recyclerViewColors.setAdapter(colorAdapter);
        recyclerViewColors.addItemDecoration(new GridDividerItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.divider_horizontal_colors), ContextCompat.getDrawable(getContext(), R.drawable.divider_horizontal_colors), 7));
    }

    private void save() {
        final Threshold threshold = new Threshold(); //TODO: This probably doesn't work
        threshold.day = day;
        threshold.startHour = startTimeHour;
        threshold.startMinute = startTimeMinute;
        threshold.endHour = endTimeHour;
        threshold.endMinute = endTimeMinute;
        threshold.temperature = seekBarTemperature.getProgress();
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
        gridtimeStart.setStartTime(threshold.startHour, threshold.startMinute);
        gridtimeEnd.setStartTime(threshold.endHour, threshold.endMinute);

        final String startTime = new DateTime()
            .withDayOfWeek(day + 1)
            .withHourOfDay(startTimeHour)
            .withMinuteOfHour(startTimeMinute)
            .toString("HH:mm");
        textViewTimeStart.setText(startTime);
        final String endTime = new DateTime()
            .withDayOfWeek(day + 1)
            .withHourOfDay(endTimeHour)
            .withMinuteOfHour(endTimeMinute)
            .toString("HH:mm");
        textViewTimeEnd.setText(endTime);

        colorAdapter.setSelectedColor(threshold.color);
    }

    private void populate(final int startMinuteX, final int maxWidth) {
        final int startMinuteInADay = Math.round(startMinuteX * (1440 / (float) maxWidth));

        final int startHour = startMinuteInADay / 60;
        final int startMinute = startMinuteInADay - startHour * 60;

        gridtimeStart.setStartTime(startHour, startMinute);
        final String startTime = new DateTime()
            .withDayOfWeek(day + 1)
            .withHourOfDay(startHour)
            .withMinuteOfHour(startMinute)
            .toString("HH:mm");
        textViewTimeStart.setText(startTime);
        final String endTime = new DateTime()
            .withDayOfWeek(day + 1)
            .withHourOfDay(startHour)
            .withMinuteOfHour(startMinute)
            .toString("HH:mm");
        textViewTimeEnd.setText(endTime);
    }

    private void showStartTimePicker() {
        gridtimeStart.show(getActivity().getSupportFragmentManager(), "GridTimePickerDialogStart");
    }

    private void showEndTimePicker() {
        gridtimeEnd.show(getActivity().getSupportFragmentManager(), "GridTimePickerDialogEnd");
    }

    private void back() {
        NavHostFragment.findNavController(this).navigate(R.id.action_thresholdFragment_to_scheduleFragment);
    }
}

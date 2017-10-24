package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SchedulePresenter;
import com.knobtviker.thermopile.presentation.views.AddDialogViewHolder;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import java.util.List;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.BindViews;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment<ScheduleContract.Presenter> implements ScheduleContract.View {
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindViews({R.id.layout_hours_monday, R.id.layout_hours_tuesday, R.id.layout_hours_wednesday, R.id.layout_hours_thursday, R.id.layout_hours_friday, R.id.layout_hours_saturday, R.id.layout_hours_sunday})
    public List<RelativeLayout> hourLayouts;

    private MainCommunicator mainCommunicator;

    public static Fragment newInstance() {
        return new ScheduleFragment();
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

        presenter = new SchedulePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        bind(this, view);

        setupToolbar();
        setupDays();
        setupDayTouchListeners();

        presenter.thresholds();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mainCommunicator.back();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mainCommunicator = null;
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

//    @Override
//    public void onThresholds(@NonNull ImmutableList<Threshold> thresholds) {
//        thresholds.forEach(this::onSaved);
//    }

//    @Override
//    public void onSaved(@NonNull Threshold threshold) {
//        final RelativeLayout layout = hourLayouts.get(threshold.day());
//
//        final Button thresholdView = new Button(layout.getContext());
//        thresholdView.setBackgroundColor(threshold.color());
//        thresholdView.setText(String.format("%s °C", String.valueOf(threshold.temperature())));
//
//        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1, 1);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.setMargins(Math.round((threshold.startHour() * 60 + threshold.startMinute()) / 2.0f), getResources().getDimensionPixelSize(R.dimen.margind_small), 0, getResources().getDimensionPixelSize(R.dimen.margind_small));
//        params.width = Math.round(
//            Minutes.minutesBetween(
//                new DateTime()
//                    .withHourOfDay(threshold.startHour())
//                    .withMinuteOfHour(threshold.startMinute()),
//                new DateTime()
//                    .withHourOfDay(threshold.endHour())
//                    .withMinuteOfHour(threshold.endMinute())
//            ).getMinutes() / 2.0f
//        );
//
//        thresholdView.setLayoutParams(params);
//        thresholdView.setOnClickListener(view -> Log.i(TAG, threshold.toString()));
//
//        layout.addView(thresholdView);
//    }

    private void setupToolbar() {
        setupCustomActionBarWithHomeAsUp(toolbar);
    }

    private void setupDays() {
//        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
//        hourLayouts
//            .forEach(relativeLayout ->
//                IntStream
//                    .range(1, realWidth + 1)
//                    .filter(i -> i % 30 == 0)
//                    .forEach(i -> {
//                        final DashedLineView view = new DashedLineView(getContext());
//                        view.setLayoutParams(params);
//                        view.setX(i);
//
//                        relativeLayout.addView(view);
//                    })
//            );
    }

    private void setupDayTouchListeners() {
        final int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        IntStream
            .range(0, hourLayouts.size())
            .forEach(index ->
                hourLayouts
                    .get(index)
                    .setOnTouchListener(new View.OnTouchListener() {
                        int startX;
                        int startY;

                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                startX = (int) event.getX();
                                startY = (int) event.getY();
                            }

                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                final int endX = (int) event.getX();
                                final int endY = (int) event.getY();
                                final int dX = Math.abs(endX - startX);
                                final int dY = Math.abs(endY - startY);


                                if (Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)) <= touchSlop) {
                                    showAddDialog(index, startX * 2);
                                }
                            }
                            return true;
                        }
                    }));
    }

    private void showAddDialog(final int dayIndex, final int minuteOfDay) {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View addDialogView = layoutInflater.inflate(R.layout.dialog_add, null, false);
        final AddDialogViewHolder addDialogViewHolder = new AddDialogViewHolder(addDialogView);
        final ColorAdapter adapter = new ColorAdapter(getContext(), addDialogViewHolder.buttonPreview);

        addDialogViewHolder.buttonPreview.setText(String.format("%s °C", String.valueOf(addDialogViewHolder.seekBarTemperature.getProgress())));
        addDialogViewHolder.seekBarTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                addDialogViewHolder.buttonPreview.setText(String.format("%s °C", String.valueOf(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //DO NOTHING
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //DO NOTHING
            }
        });
        addDialogViewHolder.recyclerViewColors.setAdapter(adapter);
        addDialogViewHolder.buttonPreview.setBackgroundTintList(ColorStateList.valueOf(adapter.getItem(0)));

        new AlertDialog.Builder(getContext())
            .setView(addDialogView)
            .setPositiveButton(getString(android.R.string.ok), (dialogInterface, i) -> {
                addThreshold(
                    dayIndex,
                    addDialogViewHolder.timePickerStart.getHour(),
                    addDialogViewHolder.timePickerStart.getMinute(),
                    addDialogViewHolder.timePickerEnd.getHour(),
                    addDialogViewHolder.timePickerEnd.getMinute(),
                    addDialogViewHolder.seekBarTemperature.getProgress(),
                    adapter.getSelectedColor(),
                    addDialogViewHolder.checkBoxModeSave.isChecked(),
                    addDialogViewHolder.editTextModeName.getText().toString().trim()
                );
                dialogInterface.dismiss();
            })
            .setNegativeButton(getString(android.R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss())
            .create()
            .show();
    }

    private void addThreshold(final int day, final int hourStart, final int minuteStart, final int hourEnd, final int minuteEnd, final int desiredTemperature, final int selectedColor, final boolean isSaveChecked, @NonNull final String modeName) {
//        presenter.save(
//            Threshold.builder()
//                .day(day)
//                .startHour(hourStart)
//                .startMinute(minuteStart)
//                .endHour(hourEnd)
//                .endMinute(minuteEnd)
//                .temperature(desiredTemperature)
//                .color(selectedColor)
//                .build()
//        );
    }
}

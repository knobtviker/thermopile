package com.knobtviker.thermopile.presentation.fragments;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ThresholdPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.ColorCommunicator;

import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by bojan on 28/10/2017.
 */

public class ThresholdFragment extends BaseFragment<ThresholdContract.Presenter> implements ThresholdContract.View, ColorCommunicator {
    public static String TAG = ThresholdFragment.class.getSimpleName();

    private int day = -1;

    private int startMinute = -1;

    private int maxWidth = -1;

    private long thresholdId = -1L;

    private ColorAdapter colorAdapter;

    @BindView(R.id.layout_temperature)
    public ConstraintLayout layoutTemperature;

    @BindView(R.id.textview_temperature)
    public TextView textViewTemperature;

    @BindView(R.id.timepicker_start)
    public TimePicker timePickerStart;

    @BindView(R.id.timepicker_end)
    public TimePicker timePickerEnd;

    @BindView(R.id.arc_temperature)
    public SeekBar seekBarTemperature;

    @BindView(R.id.recyclerview_colors)
    public RecyclerView recyclerViewColors;

    public static Fragment newInstance(final int day, final int startMinute, final int maxWidth) {
        final ThresholdFragment fragment = new ThresholdFragment();

        final Bundle arguments = new Bundle();
        arguments.putInt(Constants.KEY_DAY, day);
        arguments.putInt(Constants.KEY_START_MINUTE, startMinute);
        arguments.putInt(Constants.KEY_MAX_WIDTH, maxWidth);
        arguments.putLong(Constants.KEY_THRESHOLD_ID, -1L);

        fragment.setArguments(arguments);

        return fragment;
    }

    public static Fragment newInstance(final long thresholdId) {
        final ThresholdFragment fragment = new ThresholdFragment();

        final Bundle arguments = new Bundle();
        arguments.putInt(Constants.KEY_DAY, -1);
        arguments.putInt(Constants.KEY_START_MINUTE, -1);
        arguments.putInt(Constants.KEY_MAX_WIDTH, -1);
        arguments.putLong(Constants.KEY_THRESHOLD_ID, thresholdId);

        fragment.setArguments(arguments);

        return fragment;
    }

    public ThresholdFragment() {
        presenter = new ThresholdPresenter(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        final Optional<Bundle> argumentsOptional = Optional.ofNullable(getArguments());
        if (argumentsOptional.isPresent()) {
            final Bundle arguments = argumentsOptional.get();
            if (arguments.containsKey(Constants.KEY_DAY)) {
                day = arguments.getInt(Constants.KEY_DAY, -1);
            }
            if (arguments.containsKey(Constants.KEY_START_MINUTE)) {
                startMinute = arguments.getInt(Constants.KEY_START_MINUTE, -1);
            }
            if (arguments.containsKey(Constants.KEY_MAX_WIDTH)) {
                maxWidth = arguments.getInt(Constants.KEY_MAX_WIDTH, -1);
            }
            if (arguments.containsKey(Constants.KEY_THRESHOLD_ID)) {
                thresholdId = arguments.getLong(Constants.KEY_THRESHOLD_ID, -1L);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_threshold, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        bind(this, view);

        setupPreview();
        setupTimePickers();
        setupRecyclerView();
        setupSeekBar();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (thresholdId != -1L) {
            presenter.loadById(realm, thresholdId);
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
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onThreshold(@NonNull Threshold threshold) {
        populate(threshold);
    }

    @Override
    public void onSaved() {
        getActivity().finish();
    }

    @Override
    public void onSelectedColor(int color) {
        applyColor(color);
    }

    @OnClick({R.id.button_back, R.id.button_save})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_back:
                getActivity().finish();
                break;
            case R.id.button_save:
                save();
                break;
        }
    }

    private void setupPreview() {
        //TODO: Obey Settings units
        textViewTemperature.setText(String.format("%s °C", String.valueOf(seekBarTemperature.getProgress())));
    }

    private void setupTimePickers() {
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
    }

    private void setupSeekBar() {
        seekBarTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewTemperature.setText(String.format("%s °C", String.valueOf(i))); //TODO: This needs to be calculated with units from Settings
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
    }

    private void setupRecyclerView() {
        colorAdapter = new ColorAdapter(getContext(), this);

        recyclerViewColors.setHasFixedSize(true);
        recyclerViewColors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewColors.setAdapter(colorAdapter);
        recyclerViewColors.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.divider_horizontal_colors)));

        applyColor(colorAdapter.getItem(0));
    }

    private void save() {
        final Threshold threshold = new Threshold();
        if (thresholdId != -1L) { //Existing threshold
            threshold.id(thresholdId);
        } else { //New threshold
            final Number currentId = Realm.getDefaultInstance().where(Threshold.class).max("id");
            threshold.id(currentId == null ? 1L : currentId.longValue() + 1);
        }
        threshold.day(day);
        threshold.startHour(timePickerStart.getHour());
        threshold.startMinute(timePickerStart.getMinute());
        threshold.endHour(timePickerEnd.getHour());
        threshold.endMinute(timePickerEnd.getMinute());
        threshold.temperature(seekBarTemperature.getProgress());
        threshold.color(colorAdapter.getSelectedColor());

        presenter.save(threshold);
    }

    private void populate(@NonNull final Threshold threshold) {
        this.day = threshold.day();
        timePickerStart.setHour(threshold.startHour());
        timePickerStart.setMinute(threshold.startMinute());
        timePickerEnd.setHour(threshold.endHour());
        timePickerEnd.setMinute(threshold.endMinute());
        seekBarTemperature.setProgress(threshold.temperature());
//        colorAdapter.setSelectedColor(threshold.color());

        applyColor(threshold.color());
    }

    private void populate(final int startMinuteX, final int maxWidth) {
        final int startMinuteInADay = Math.round(startMinuteX * (1440 / (float) maxWidth));

        final int startHour = startMinuteInADay / 60;
        final int startMinute = startMinuteInADay - startHour * 60;

        timePickerStart.setHour(startHour);
        timePickerStart.setMinute(startMinute);
    }

    private void applyColor(final int color) {
        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(getResources().getDimensionPixelSize(R.dimen.corner_24dp)*2);
        shapeDrawable.setIntrinsicWidth(getResources().getDimensionPixelSize(R.dimen.corner_24dp)*2);
        shapeDrawable.getPaint().setColor(color);

        final GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {color, 0x00000000});
        gradientDrawable.setAlpha(211);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.corner_24dp));

        textViewTemperature.setBackground(shapeDrawable);
        layoutTemperature.setBackground(gradientDrawable);
    }
}

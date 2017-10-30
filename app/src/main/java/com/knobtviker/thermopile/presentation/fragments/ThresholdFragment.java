package com.knobtviker.thermopile.presentation.fragments;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toolbar;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.ThresholdPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;

import java.util.Optional;

import butterknife.BindView;
import io.realm.Realm;

/**
 * Created by bojan on 28/10/2017.
 */

public class ThresholdFragment extends BaseFragment<ThresholdContract.Presenter> implements ThresholdContract.View {
    public static String TAG = ThresholdFragment.class.getSimpleName();

    private int day = -1;

    private int startMinute = -1;

    private int maxWidth = -1;

    private long thresholdId = -1L;

    private ColorAdapter colorAdapter;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.button_preview)
    public Button buttonPreview;

    @BindView(R.id.timepicker_start)
    public TimePicker timePickerStart;

    @BindView(R.id.timepicker_end)
    public TimePicker timePickerEnd;

    @BindView(R.id.seekbar_temperature)
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

        presenter = new ThresholdPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_threshold, container, false);

        super.bind(this, view);

        setupToolbar();
        setupPreview();
        setupTimePickers();
        setupRecyclerView();
        setupSeekBar();

        if (thresholdId != -1L) {
            presenter.loadById(thresholdId);
        } else if (day != -1 && startMinute != -1 && maxWidth != -1) {
            populate(startMinute, maxWidth);
        } else {
            //TODO: Show some impossible error
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.threshold, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            save();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    private void setupToolbar() {
        setupCustomActionBarWithHomeAsUp(toolbar);
    }

    private void setupPreview() {
        buttonPreview.setText(String.format("%s °C", String.valueOf(seekBarTemperature.getProgress())));
    }

    private void setupTimePickers() {
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
    }

    private void setupSeekBar() {
        seekBarTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                buttonPreview.setText(String.format("%s °C", String.valueOf(i))); //TODO: This needs to be calculated with units from Settings
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
        buttonPreview.setBackgroundTintList(ColorStateList.valueOf(colorAdapter.getItem(0)));
    }

    private void setupRecyclerView() {
        colorAdapter = new ColorAdapter(getContext(), buttonPreview);

        recyclerViewColors.setHasFixedSize(true);
        recyclerViewColors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewColors.setAdapter(colorAdapter);
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
        colorAdapter.setSelectedColor(threshold.color());
    }

    private void populate(final int startMinuteX, final int maxWidth) {
        final int startMinuteInADay = Math.round(startMinuteX * (1440 / (float) maxWidth));

        final int startHour = startMinuteInADay / 60;
        final int startMinute = startMinuteInADay - startHour * 60;

        timePickerStart.setHour(startHour);
        timePickerStart.setMinute(startMinute);
    }
}

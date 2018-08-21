package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.presenters.SchedulePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.MathKit;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdViewHolder;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindViews;
import butterknife.OnClick;
import timber.log.Timber;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.START;
import static android.support.constraint.ConstraintSet.TOP;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment<ScheduleContract.Presenter> implements ScheduleContract.View {
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    @FormatDate
    private String formatDate;

    @UnitTemperature
    private int unitTemperature;

    private List<Threshold> thresholds = new ArrayList<>(0);

    @BindViews({R.id.layout_hours_monday, R.id.layout_hours_tuesday, R.id.layout_hours_wednesday, R.id.layout_hours_thursday, R.id.layout_hours_friday, R.id.layout_hours_saturday, R.id.layout_hours_sunday})
    public List<ConstraintLayout> weekdayLayouts;

    @BindViews({R.id.textview_day_monday, R.id.textview_day_tuesday, R.id.textview_day_wednesday, R.id.textview_day_thursday, R.id.textview_day_friday, R.id.textview_day_saturday, R.id.textview_day_sunday})
    public List<TextView> weekdayTextViews;

    public ScheduleFragment() {
        formatDate = FormatDate.EEEE_DD_MM_YYYY;
        unitTemperature = UnitTemperature.CELSIUS;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDayTouchListeners();

        presenter = new SchedulePresenter(this);

        load();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        formatDate = settings.formatDate;
        unitTemperature = settings.unitTemperature;

        setWeekdayNames(formatDate);
        repopulate();
    }

    @Override
    public void onThresholds(@NonNull List<Threshold> thresholds) {
        this.thresholds = thresholds;

        populate(thresholds);
    }

    @OnClick({R.id.button_back, R.id.button_add})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_back:
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case R.id.button_add:
                add();
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDayTouchListeners() {
        final int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        IntStream
            .range(0, weekdayLayouts.size())
            .forEach(index ->
                weekdayLayouts
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
                                    navigateToThreshold(index, startX, weekdayLayouts.get(0).getWidth());
                                }
                            }
                            return true;
                        }
                    }));
    }

    private void load() {
        presenter.settings();
        presenter.thresholds();
    }

    private void populate(@NonNull final List<Threshold> thresholds) {
        weekdayLayouts.forEach(ViewGroup::removeAllViewsInLayout);

        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        thresholds
            .forEach(threshold -> {
                final ConstraintLayout layout = weekdayLayouts.get(threshold.day);

                final View thresholdView = layoutInflater.inflate(R.layout.item_threshold, null);

                final ThresholdViewHolder thresholdViewHolder = ThresholdViewHolder.bind(
                    thresholdView,
                    threshold.color,
                    buildThresholdTemperature(threshold.temperature)
                );

                thresholdView.setId(View.generateViewId());

                thresholdViewHolder.rootLayout.setOnClickListener(v -> {
                    if (thresholdViewHolder.getState() == ThresholdViewHolder.State.STATE_DEFAULT) {
                        NavHostFragment
                            .findNavController(this)
                            .navigate(
                                ScheduleFragmentDirections
                                    .showThresholdByIdAction()
                                    .setThresholdId((int) threshold.id)
                            );
                    } else {
                        thresholdViewHolder.setState(ThresholdViewHolder.State.STATE_DEFAULT);
                    }
                });
                thresholdViewHolder.buttonRemove.setOnClickListener(v -> presenter.removeThresholdById(threshold.id));
                thresholdViewHolder.rootLayout.setOnLongClickListener(v -> {
                    thresholdViewHolder.setState(ThresholdViewHolder.State.STATE_REMOVE);
                    return true;
                });
                thresholdViewHolder.textViewTemperature.setOnLongClickListener(v -> {
                    thresholdViewHolder.setState(ThresholdViewHolder.State.STATE_REMOVE);
                    return true;
                });

                layout.addView(thresholdView);

                final ConstraintSet set = new ConstraintSet();
                set.clone(layout);
                set.constrainWidth(thresholdView.getId(), Math.round(
                    Minutes.minutesBetween(
                        new DateTime()
                            .withHourOfDay(threshold.startHour)
                            .withMinuteOfHour(threshold.startMinute),
                        new DateTime()
                            .withHourOfDay(threshold.endHour)
                            .withMinuteOfHour(threshold.endMinute)
                    ).getMinutes() / 2.0f
                ));
                set.constrainHeight(thresholdView.getId(), ConstraintSet.MATCH_CONSTRAINT);
                set.connect(thresholdView.getId(), START, ConstraintSet.PARENT_ID, START, Math.round((threshold.startHour * 60 + threshold.startMinute) / 2.0f));
                set.connect(thresholdView.getId(), TOP, ConstraintSet.PARENT_ID, TOP, 8);
                set.connect(thresholdView.getId(), BOTTOM, ConstraintSet.PARENT_ID, BOTTOM, 8);

                set.applyTo(layout);
            });
    }

    private void repopulate() {
        if (!thresholds.isEmpty()) {
            populate(thresholds);
        }
    }

    private void setWeekdayNames(@FormatDate @NonNull final String formatDate) {
        final List<String> days = Arrays.asList(getResources().getStringArray(R.array.weekdays));
        final List<String> daysShort = Arrays.asList(getResources().getStringArray(R.array.weekdays_short));

        weekdayTextViews
            .forEach(textView -> {
                switch (textView.getId()) {
                    case R.id.textview_day_monday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(0));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(0));
                        } else {
                            textView.setText(days.get(0));
                        }
                        break;
                    case R.id.textview_day_tuesday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(1));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(1));
                        } else {
                            textView.setText(days.get(1));
                        }
                        break;
                    case R.id.textview_day_wednesday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(2));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(2));
                        } else {
                            textView.setText(days.get(2));
                        }
                        break;
                    case R.id.textview_day_thursday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(3));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(3));
                        } else {
                            textView.setText(days.get(3));
                        }
                        break;
                    case R.id.textview_day_friday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(4));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(4));
                        } else {
                            textView.setText(days.get(4));
                        }
                        break;
                    case R.id.textview_day_saturday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(5));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(5));
                        } else {
                            textView.setText(days.get(5));
                        }
                        break;
                    case R.id.textview_day_sunday:
                        if (formatDate.contains("EEEE")) {
                            textView.setText(days.get(6));
                        } else if (formatDate.contains("EE")) {
                            textView.setText(daysShort.get(6));
                        } else {
                            textView.setText(days.get(6));
                        }
                        break;
                }
            });
    }

    private void add() {
        final CharSequence[] days = getResources().getStringArray(R.array.weekdays);

        new AlertDialog.Builder(getContext())
            .setTitle(R.string.label_select_day)
            .setCancelable(true)
            .setSingleChoiceItems(days, -1, (dialogInterface, index) -> {
                dialogInterface.dismiss();
                navigateToThreshold(index, 0, weekdayLayouts.get(0).getWidth());
            })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
            .create()
            .show();
    }

    private String buildThresholdTemperature(final int value) {
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

        return String.format("%s %s", String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))), unit);
    }

    private void navigateToThreshold(int day, int startX, int width) {
        NavHostFragment
            .findNavController(this)
            .navigate(
                ScheduleFragmentDirections
                    .showThresholdByIdAction()
                    .setDay(day)
                    .setStartMinute(startX)
                    .setMaxWidth(width)
            );
    }
}

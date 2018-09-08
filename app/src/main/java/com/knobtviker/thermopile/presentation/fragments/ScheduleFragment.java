package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.ThresholdChip;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdViewHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.BindViews;
import timber.log.Timber;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.START;
import static android.support.constraint.ConstraintSet.TOP;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment<ScheduleContract.Presenter> implements ScheduleContract.View {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindViews({R.id.layout_hours_monday, R.id.layout_hours_tuesday, R.id.layout_hours_wednesday, R.id.layout_hours_thursday,
        R.id.layout_hours_friday, R.id.layout_hours_saturday, R.id.layout_hours_sunday})
    public List<ConstraintLayout> weekdayLayouts;

    @BindViews({R.id.textview_day_monday, R.id.textview_day_tuesday, R.id.textview_day_wednesday, R.id.textview_day_thursday,
        R.id.textview_day_friday, R.id.textview_day_saturday, R.id.textview_day_sunday})
    public List<TextView> weekdayTextViews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupDayTouchListeners();
    }

    @Override
    protected void load() {
        presenter.load();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onThresholds(@NonNull List<ThresholdChip> thresholds) {
        weekdayLayouts.forEach(ViewGroup::removeAllViewsInLayout);

        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        thresholds
            .forEach(threshold -> {
                final ConstraintLayout layout = weekdayLayouts.get(threshold.day());

                final View thresholdView = layoutInflater.inflate(R.layout.item_threshold, null);

                final ThresholdViewHolder thresholdViewHolder = ThresholdViewHolder.bind(
                    thresholdView,
                    threshold.color(),
                    threshold.temperature()
                );

                thresholdView.setId(View.generateViewId());

                thresholdViewHolder.rootLayout.setOnClickListener(v -> {
                    if (thresholdViewHolder.getState() == ThresholdViewHolder.State.STATE_DEFAULT) {
                        showThresholdbyId(threshold.id());
                    } else {
                        thresholdViewHolder.setState(ThresholdViewHolder.State.STATE_DEFAULT);
                    }
                });
                thresholdViewHolder.buttonRemove.setOnClickListener(v -> presenter.removeThresholdById(threshold.id()));
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
                set.constrainWidth(thresholdView.getId(), threshold.width());
                set.constrainHeight(thresholdView.getId(), ConstraintSet.MATCH_CONSTRAINT);
                set.connect(thresholdView.getId(), START, ConstraintSet.PARENT_ID, START, threshold.offset());
                set.connect(thresholdView.getId(), TOP, ConstraintSet.PARENT_ID, TOP, 8);
                set.connect(thresholdView.getId(), BOTTOM, ConstraintSet.PARENT_ID, BOTTOM, 8);
                set.applyTo(layout);
            });
    }

    @Override
    public void setWeekdayNames(boolean isShortName) {
        final List<String> days = Arrays.asList(getResources().getStringArray(isShortName ? R.array.weekdays_short : R.array.weekdays));
        IntStream
            .range(0, 7)
            .forEach(day -> weekdayTextViews.get(day).setText(days.get(day)));
    }

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.schedule);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.showAddDialog:
                    add();
                    return true;
                default:
                    return false;
            }
        });
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(this));
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
                                    navigateToCreateThreshold(index, startX, weekdayLayouts.get(0).getWidth());
                                }
                            }
                            return true;
                        }
                    }));
    }

    private void add() {
        final CharSequence[] days = getResources().getStringArray(R.array.weekdays);

        new AlertDialog.Builder(getContext())
            .setTitle(R.string.label_select_day)
            .setCancelable(true)
            .setSingleChoiceItems(days, -1, (dialogInterface, index) -> {
                dialogInterface.dismiss();
                navigateToCreateThreshold(index, 0, weekdayLayouts.get(0).getWidth());
            })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
            .create()
            .show();
    }

    private void navigateToCreateThreshold(int day, int startX, int width) {
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

    private void showThresholdbyId(final long thresholdId) {
        NavHostFragment
            .findNavController(this)
            .navigate(
                ScheduleFragmentDirections
                    .showThresholdByIdAction()
                    .setThresholdId(thresholdId)
            );
    }
}

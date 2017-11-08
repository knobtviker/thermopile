package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SchedulePresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.BindViews;
import io.realm.RealmResults;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.START;
import static android.support.constraint.ConstraintSet.TOP;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment<ScheduleContract.Presenter> implements ScheduleContract.View {
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindViews({R.id.textview_hour_00, R.id.textview_hour_01, R.id.textview_hour_02, R.id.textview_hour_03, R.id.textview_hour_04, R.id.textview_hour_05, R.id.textview_hour_06, R.id.textview_hour_07, R.id.textview_hour_08, R.id.textview_hour_09, R.id.textview_hour_10, R.id.textview_hour_11, R.id.textview_hour_12, R.id.textview_hour_13, R.id.textview_hour_14, R.id.textview_hour_15, R.id.textview_hour_16, R.id.textview_hour_17, R.id.textview_hour_18, R.id.textview_hour_19, R.id.textview_hour_20, R.id.textview_hour_21, R.id.textview_hour_22, R.id.textview_hour_23})
    public List<TextView> textViewHours;

    @BindViews({R.id.textview_minute_00, R.id.textview_minute_01, R.id.textview_minute_02, R.id.textview_minute_03, R.id.textview_minute_04, R.id.textview_minute_05, R.id.textview_minute_06, R.id.textview_minute_07, R.id.textview_minute_08, R.id.textview_minute_09, R.id.textview_minute_10, R.id.textview_minute_11, R.id.textview_minute_12, R.id.textview_minute_13, R.id.textview_minute_14, R.id.textview_minute_15, R.id.textview_minute_16, R.id.textview_minute_17, R.id.textview_minute_18, R.id.textview_minute_19, R.id.textview_minute_20, R.id.textview_minute_21, R.id.textview_minute_22, R.id.textview_minute_23})
    public List<TextView> textViewMinutes;

    @BindViews({R.id.layout_hours_monday, R.id.layout_hours_tuesday, R.id.layout_hours_wednesday, R.id.layout_hours_thursday, R.id.layout_hours_friday, R.id.layout_hours_saturday, R.id.layout_hours_sunday})
    public List<ConstraintLayout> hourLayouts;

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
        setupDayTouchListeners();

        presenter.settings();
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
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            add();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        mainCommunicator = null;

        super.onDetach();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        IntStream
            .range(0, textViewMinutes.size())
            .forEach(i -> {
                if (settings.formatClock() == Constants.CLOCK_MODE_12H) {
                    if (i == 0) {
                        textViewHours.get(i).setText(String.valueOf(i + 12));
                    } else {
                        textViewHours.get(i).setText(i < 13 ? (i < 10 ? String.format(Locale.getDefault(), "0%d", i) : String.valueOf(i)) : (i - 12 < 10 ? String.format(Locale.getDefault(), "0%d", i - 12) : String.valueOf(i - 12)));
                    }
                    textViewMinutes.get(i).setText(getString(i < 12 ? R.string.am : R.string.pm));
                } else {
                    textViewHours.get(i).setText(i < 10 ? String.format(Locale.getDefault(), "0%d", i) : String.valueOf(i));
                    textViewMinutes.get(i).setText(getString(R.string._00));
                }
            });
    }

    @Override
    public void onThresholds(@NonNull RealmResults<Threshold> thresholds) {
        populate(thresholds);
    }

    private void setupToolbar() {
        setupCustomActionBarWithHomeAsUp(toolbar);
    }

    @SuppressLint("ClickableViewAccessibility")
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
                                    Router.showThreshold(getContext(), index, startX, hourLayouts.get(0).getWidth());
                                }
                            }
                            return true;
                        }
                    }));
    }

    private void populate(@NonNull final RealmResults<Threshold> thresholds) {
        hourLayouts.forEach(ViewGroup::removeAllViewsInLayout);

        thresholds
            .forEach(threshold -> {
                final ConstraintLayout layout = hourLayouts.get(threshold.day());

                final Button thresholdView = new Button(layout.getContext());
                thresholdView.setId(View.generateViewId());
                thresholdView.setBackgroundColor(threshold.color());
                thresholdView.setText(String.format("%s °C", String.valueOf(threshold.temperature()))); //TODO: Obey Settings temperature unit

                layout.addView(thresholdView);

                final ConstraintSet set = new ConstraintSet();
                set.clone(layout);
                set.constrainWidth(thresholdView.getId(), Math.round(
                    Minutes.minutesBetween(
                        new DateTime()
                            .withHourOfDay(threshold.startHour())
                            .withMinuteOfHour(threshold.startMinute()),
                        new DateTime()
                            .withHourOfDay(threshold.endHour())
                            .withMinuteOfHour(threshold.endMinute())
                    ).getMinutes() / 2.0f
                ));
                set.constrainHeight(thresholdView.getId(), ConstraintSet.MATCH_CONSTRAINT);
                set.connect(thresholdView.getId(), START, ConstraintSet.PARENT_ID, START, Math.round((threshold.startHour() * 60 + threshold.startMinute()) / 2.0f));
                set.connect(thresholdView.getId(), TOP, ConstraintSet.PARENT_ID, TOP, 8);
                set.connect(thresholdView.getId(), BOTTOM, ConstraintSet.PARENT_ID, BOTTOM, 8);

                set.applyTo(layout);

                thresholdView.setOnClickListener(view -> Router.showThreshold(getContext(), threshold.id()));
            });
    }

    private void add() {
        final CharSequence[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        new AlertDialog.Builder(getContext())
            .setTitle("Select a day")
            .setCancelable(true)
            .setSingleChoiceItems(days, -1, (dialogInterface, index) -> {
                dialogInterface.dismiss();
                Router.showThreshold(getContext(), index, 0, hourLayouts.get(0).getWidth());
            })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
            .create()
            .show();
    }
}

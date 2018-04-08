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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SchedulePresenter;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;
import com.knobtviker.thermopile.presentation.views.viewholders.ThresholdViewHolder;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.List;
import java.util.stream.IntStream;

import butterknife.BindViews;
import butterknife.OnClick;
import io.realm.RealmResults;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.START;
import static android.support.constraint.ConstraintSet.TOP;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment<ScheduleContract.Presenter> implements ScheduleContract.View {
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    @BindViews({R.id.layout_hours_monday, R.id.layout_hours_tuesday, R.id.layout_hours_wednesday, R.id.layout_hours_thursday, R.id.layout_hours_friday, R.id.layout_hours_saturday, R.id.layout_hours_sunday})
    public List<ConstraintLayout> hourLayouts;

    private MainCommunicator mainCommunicator;

    public static Fragment newInstance() {
        return new ScheduleFragment();
    }

    public ScheduleFragment() {
        presenter = new SchedulePresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainCommunicator) {
            mainCommunicator = (MainCommunicator) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDayTouchListeners();
    }

    @Override
    public void onResume() {
        presenter.settings(realm);
        presenter.thresholds(realm);
        super.onResume();
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
//        IntStream
//            .range(0, textViewMinutes.size())
//            .forEach(i -> {
//                if (settings.formatClock() == Constants.CLOCK_MODE_12H) {
//                    if (i == 0) {
//                        textViewHours.get(i).setText(String.valueOf(i + 12));
//                    } else {
//                        textViewHours.get(i).setText(i < 13 ? (i < 10 ? String.format(Locale.getDefault(), "0%d", i) : String.valueOf(i)) : (i - 12 < 10 ? String.format(Locale.getDefault(), "0%d", i - 12) : String.valueOf(i - 12)));
//                    }
//                    textViewMinutes.get(i).setText(getString(i < 12 ? R.string.am : R.string.pm));
//                } else {
//                    textViewHours.get(i).setText(i < 10 ? String.format(Locale.getDefault(), "0%d", i) : String.valueOf(i));
//                    textViewMinutes.get(i).setText(getString(R.string._00));
//                }
//            });
    }

    @Override
    public void onThresholds(@NonNull RealmResults<Threshold> thresholds) {
        populate(thresholds);
    }

    @OnClick({R.id.button_back, R.id.button_add})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_back:
                if (mainCommunicator != null) {
                    mainCommunicator.showMain();
                }
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

        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        thresholds
            .forEach(threshold -> {
                final ConstraintLayout layout = hourLayouts.get(threshold.day());

                final View thresholdView = layoutInflater.inflate(R.layout.item_threshold, null);
                final ThresholdViewHolder thresholdViewHolder = new ThresholdViewHolder(thresholdView);

                thresholdView.setId(View.generateViewId());

                thresholdViewHolder.setBackground(getContext().getColor(threshold.color()), layout.getHeight());
                thresholdViewHolder.textViewTemperature.setText(String.format("%s °C", String.valueOf(threshold.temperature()))); //TODO: Obey Settings temperature unit

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

                thresholdViewHolder.rootLayout.setOnClickListener(view -> Router.showThreshold(getContext(), threshold.id()));
            });
    }

    private void add() {
        final CharSequence[] days = getResources().getStringArray(R.array.weekdays);

        new AlertDialog.Builder(getContext())
            .setTitle(R.string.label_select_day)
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

package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.views.DashedLineView;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import java.util.List;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment {
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.layout_hours)
    public LinearLayout layoutHours;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        bind(this, view);

        setupToolbar();
        setupDays();
        setupDayTouchListeners();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.modes, menu);
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

    private void setupToolbar() {
        setupCustomActionBarWithHomeAsUp(toolbar);
    }

    private void setupDays() {
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final RelativeLayout hourLayout = hourLayouts.get(0);
        final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) hourLayout.getLayoutParams();
        final float weight = layoutParams.weight;
        final int realWidth = Math.round(screenWidth * weight);

        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        IntStream
            .range(0, 24)
            .forEach(hour -> {
                final View view = layoutInflater.inflate(R.layout.item_hour_toolbar, null);
                final TextView textViewHour = ButterKnife.findById(view, R.id.textview_hour);
                textViewHour.setText(String.format(hour < 10 ? "0%s" : "%s", String.valueOf(hour)));
                layoutHours.addView(view);
            });

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        hourLayouts
            .forEach(relativeLayout ->
                IntStream
                    .range(1, realWidth + 1)
                    .filter(i -> i % 30 == 0)
                    .forEach(i -> {
                        final DashedLineView view = new DashedLineView(getContext());
                        view.setLayoutParams(params);
                        view.setX(i);

                        relativeLayout.addView(view);
                    })
            );
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
                                    showAddDialog(index, startX*2);
                                }
                            }
                            return true;
                        }
                    }));
    }

    private void showAddDialog(final int dayIndex, final int minuteOfDay) {
        Log.i(TAG, dayIndex + " --- " + minuteOfDay);
        new AlertDialog.Builder(getContext())
            .setTitle("OVO ĆE POSTAVITI TEMP")
            .setMessage("OVO ĆE POSTAVITI TEMP SA MINUTOM I SATOM START I END I SLIDER ZA TEMP I COLOR PICKER MATERIAL OSNOVNI")
            .setPositiveButton("SET", (dialogInterface, i) -> {
                //TODO: Save this range and mode and show on day view
                dialogInterface.dismiss();
            })
            .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
            .create()
            .show();
    }
}

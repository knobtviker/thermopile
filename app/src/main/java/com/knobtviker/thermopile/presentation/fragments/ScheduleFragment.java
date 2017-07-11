package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
        final int MINUTES_IN_DAY = 1440;

        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        IntStream
            .range(0, 24)
            .forEach(hour -> {
                final View view = layoutInflater.inflate(R.layout.item_hour_toolbar, null);
                final TextView textViewHour = ButterKnife.findById(view, R.id.textview_hour);
                textViewHour.setText(String.format(hour < 10 ? "0%s" : "%s", String.valueOf(hour)));
                Log.i(TAG, textViewHour.getText().toString());
//                view.setScaleX(0.75f);
//                view.setScaleY(0.75f);
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
}

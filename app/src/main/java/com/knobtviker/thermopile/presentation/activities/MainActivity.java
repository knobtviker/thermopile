package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApp;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

public class MainActivity extends BaseActivity implements MainCommunicator {

    private Fragment mainFragment;
    private Fragment settingsFragment;
    private Fragment scheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainFragment = MainFragment.newInstance();
        this.settingsFragment = SettingsFragment.newInstance();
        this.scheduleFragment = ScheduleFragment.newInstance();

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.fragment_container, mainFragment)
            .add(R.id.fragment_container, settingsFragment)
            .add(R.id.fragment_container, scheduleFragment)
            .show(mainFragment)
            .hide(settingsFragment)
            .hide(scheduleFragment)
            .commitNowAllowingStateLoss();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ((ThermopileApp) getApplication()).destroyScreensaver();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            ((ThermopileApp) getApplication()).createScreensaver();
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void showMain() {
        getSupportFragmentManager()
            .beginTransaction()
            .show(mainFragment)
            .hide(settingsFragment)
            .hide(scheduleFragment)
            .commitNowAllowingStateLoss();
    }

    @Override
    public void showSchedule() {
        getSupportFragmentManager()
            .beginTransaction()
            .hide(mainFragment)
            .hide(settingsFragment)
            .show(scheduleFragment)
            .commitNowAllowingStateLoss();
    }

    @Override
    public void showSettings() {
        getSupportFragmentManager()
            .beginTransaction()
            .hide(mainFragment)
            .show(settingsFragment)
            .hide(scheduleFragment)
            .commitNowAllowingStateLoss();
    }

    public Fragment mainFragment() {
        return mainFragment;
    }
}

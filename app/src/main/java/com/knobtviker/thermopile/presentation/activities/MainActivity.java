package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.knobtviker.thermopile.presentation.ThermopileApp;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

public class MainActivity extends BaseActivity implements MainCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showMainFragment();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ((ThermopileApp)getApplication()).destroyScreensaver();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            ((ThermopileApp)getApplication()).createScreensaver();
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void back() {
        popBackStack();
    }

    @Override
    public void showSchedule() {
        Fragment fragment = findFragment(ScheduleFragment.TAG);
        if (fragment == null) {
            fragment = ScheduleFragment.newInstance();
        }

        addFragment(fragment, ScheduleFragment.TAG, true);
    }

    @Override
    public void showSettings() {
        Fragment fragment = findFragment(SettingsFragment.TAG);
        if (fragment == null) {
            fragment = SettingsFragment.newInstance();
        }

        addFragment(fragment, SettingsFragment.TAG, true);
    }

    private void showMainFragment() {
        Fragment fragment = findFragment(MainFragment.TAG);
        if (fragment == null) {
            fragment = MainFragment.newInstance();
        }

        replaceFragment(fragment, MainFragment.TAG, false);
    }
}

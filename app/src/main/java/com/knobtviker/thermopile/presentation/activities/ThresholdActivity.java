package com.knobtviker.thermopile.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApp;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;
import com.knobtviker.thermopile.presentation.fragments.ThresholdFragment;
import com.knobtviker.thermopile.presentation.utils.Constants;

/**
 * Created by bojan on 28/10/2017.
 */

public class ThresholdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int day = -1;
        int startMinute = -1;
        int maxWidth = -1;
        long thresholdId = -1L;

        final Intent intent = getIntent();
        if (intent.hasExtra(Constants.KEY_DAY)) {
            day = intent.getIntExtra(Constants.KEY_DAY, -1);
        }
        if (intent.hasExtra(Constants.KEY_START_MINUTE)) {
            startMinute = intent.getIntExtra(Constants.KEY_START_MINUTE, -1);
        }
        if (intent.hasExtra(Constants.KEY_MAX_WIDTH)) {
            maxWidth = intent.getIntExtra(Constants.KEY_MAX_WIDTH, -1);
        }
        if (intent.hasExtra(Constants.KEY_THRESHOLD_ID)) {
            thresholdId = intent.getLongExtra(Constants.KEY_THRESHOLD_ID, -1L);
        }

        Fragment thresholdFragment = null;
        if (day != -1 && startMinute != -1 && maxWidth != -1) {
            thresholdFragment = ThresholdFragment.newInstance(day, startMinute, maxWidth);
        } else if (thresholdId != -1L) {
            thresholdFragment = ThresholdFragment.newInstance(thresholdId);
        } else {
            finish();
        }

        if (thresholdFragment != null) {
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, thresholdFragment)
                .commitNowAllowingStateLoss();
        }
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
}

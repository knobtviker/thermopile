package com.knobtviker.thermopile.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

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

        if (day != -1 && startMinute != -1 && maxWidth != -1) {
            showThresholdFragment(day, startMinute, maxWidth);
        } else if (thresholdId != -1L) {
            showThresholdFragment(thresholdId);
        } else {
            finish();
        }
    }

    private void showThresholdFragment(final int day, final int startMinute, final int maxWidth) {
        Fragment fragment = findFragment(ThresholdFragment.TAG);
        if (fragment == null) {
            fragment = ThresholdFragment.newInstance(day, startMinute, maxWidth);
        }

        replaceFragment(fragment, ThresholdFragment.TAG, false);
    }

    private void showThresholdFragment(final long thresholdId) {
        Fragment fragment = findFragment(ThresholdFragment.TAG);
        if (fragment == null) {
            fragment = ThresholdFragment.newInstance(thresholdId);
        }

        replaceFragment(fragment, ThresholdFragment.TAG, false);
    }
}

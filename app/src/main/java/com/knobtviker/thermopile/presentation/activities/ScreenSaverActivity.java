package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;
import com.knobtviker.thermopile.presentation.fragments.ScreensaverFragment;

import timber.log.Timber;

/**
 * Created by bojan on 11/06/2017.
 */

public class ScreenSaverActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        brightnessMin();

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.fragment_container, ScreensaverFragment.newInstance())
            .commitNowAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        brightnessMax();

        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ((ThermopileApplication)getApplication()).createScreensaver();
            finish();
        }
        return super.dispatchTouchEvent(event);
    }

    private void brightnessMin() {
        brightness(0.1f);
    }

    private void brightnessMax() {
        brightness(1.0f);
    }

    private void brightness(final float value) {
        final WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = value;
        getWindow().setAttributes(layoutParams);
        Timber.i("brightness set to %s", value);
    }
}

package com.knobtviker.thermopile.presentation.shared.base;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

/**
 * Created by bojan on 10/06/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    // Possible solution to DayNight mixed resources issue.
    // https://issuetracker.google.com/issues/37083803
    // https://issuetracker.google.com/issues/37110398
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        final int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//
//        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
//            newConfig.uiMode = (newConfig.uiMode & ~Configuration.UI_MODE_NIGHT_MASK) | Configuration.UI_MODE_NIGHT_YES;
//        }
//
//        super.onConfigurationChanged(newConfig);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.getClass().getSimpleName().equalsIgnoreCase(ScreenSaverActivity.class.getSimpleName())) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                ((ThermopileApplication) getApplication()).createScreensaver();
                finish();
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ((ThermopileApplication) getApplication()).destroyScreensaver();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                ((ThermopileApplication) getApplication()).createScreensaver();
            }
        }

        return super.dispatchTouchEvent(event);
    }
}

package com.knobtviker.thermopile.presentation.shared.base;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MotionEvent;

import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Preferences;

/**
 * Created by bojan on 10/06/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(
            PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext())
                .getInt(Preferences.THEME, Default.THEME)
        );

        super.onCreate(savedInstanceState);
    }

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

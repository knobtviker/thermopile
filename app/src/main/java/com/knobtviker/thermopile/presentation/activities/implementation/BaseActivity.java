package com.knobtviker.thermopile.presentation.activities.implementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.instabug.library.InstabugTrackingDelegate;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

/**
 * Created by bojan on 10/06/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        InstabugTrackingDelegate.notifyActivityGotTouchEvent(event, this);

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

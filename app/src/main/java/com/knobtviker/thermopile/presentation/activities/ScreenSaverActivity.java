package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApp;

/**
 * Created by bojan on 11/06/2017.
 */

public class ScreenSaverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ((ThermopileApp)getApplication()).createScreensaver();
            finish();
        }
        return super.onTouchEvent(event);
    }
}

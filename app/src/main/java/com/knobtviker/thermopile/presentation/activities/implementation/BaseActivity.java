package com.knobtviker.thermopile.presentation.activities.implementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.instabug.library.InstabugTrackingDelegate;
import com.knobtviker.thermopile.R;

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InstabugTrackingDelegate.notifyActivityGotTouchEvent(ev, this);
        return super.dispatchTouchEvent(ev);
    }
}

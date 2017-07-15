package com.knobtviker.thermopile.presentation;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by bojan on 15/07/2017.
 */

public class ThermopileApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initJodaTime();
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }
}

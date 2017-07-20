package com.knobtviker.thermopile.presentation;

import android.app.Application;

import com.facebook.stetho.Stetho;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by bojan on 15/07/2017.
 */

public class ThermopileApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initJodaTime();
        initStetho();
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }
}

package com.knobtviker.thermopile.data.sources.local.implementation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.MyObjectBox;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.utils.Constants;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class Database {

    private static BoxStore INSTANCE = null;

    public static void init(@NonNull Context context) {
        INSTANCE = MyObjectBox.builder()
            .androidContext(context)
            .name(BuildConfig.DATABASE_NAME)
            .build();

        final boolean started = new AndroidObjectBrowser(INSTANCE).start(context);

        if (INSTANCE.boxFor(Settings.class).count() == 0) {
            INSTANCE.boxFor(Settings.class).put(defaultSettings());
        }
    }

    public static BoxStore getInstance() {
        return INSTANCE;
    }

    private static Settings defaultSettings() {
        return new Settings(
            Constants.DEFAULT_TIMEZONE,
            Constants.CLOCK_MODE_24H,
            Constants.DEFAULT_FORMAT_DATE,
            Constants.FORMAT_TIME_LONG_24H,
            Constants.UNIT_TEMPERATURE_CELSIUS,
            Constants.UNIT_PRESSURE_PASCAL,
            Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2,
            Constants.DEFAULT_SCREENSAVER_DELAY,
            AppCompatDelegate.MODE_NIGHT_AUTO
        );
    }
}

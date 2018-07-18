package com.knobtviker.thermopile.data.sources.local.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.MyObjectBox;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.utils.constants.integrity.Default;

import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.DebugFlags;
import io.objectbox.android.AndroidObjectBrowser;

public class Database {

    private static BoxStore INSTANCE = null;

    public static void init(@NonNull Context context) {
        final BoxStoreBuilder builder = MyObjectBox.builder()
            .androidContext(context)
            .name(BuildConfig.DATABASE_NAME);
        
        if (BuildConfig.DEBUG_LOCAL) {
            builder
                .debugFlags(DebugFlags.LOG_TRANSACTIONS_READ | DebugFlags.LOG_TRANSACTIONS_WRITE)
                .debugRelations();
        }

        INSTANCE = builder.build();

        if (BuildConfig.DEBUG && BuildConfig.DEBUG_LOCAL) {
            final boolean started = new AndroidObjectBrowser(INSTANCE).start(context);
        }

        if (INSTANCE.boxFor(Settings.class).count() == 0) {
            INSTANCE.boxFor(Settings.class).put(defaultSettings());
        }
    }

    public static BoxStore getInstance() {
        return INSTANCE;
    }

    public static Settings defaultSettings() {
        return new Settings(
            Default.TIMEZONE,
            Default.CLOCK_MODE,
            Default.FORMAT_DATE,
            Default.FORMAT_TIME,
            Default.UNIT_TEMPERATURE,
            Default.UNIT_PRESSURE,
            Default.UNIT_ACCELERATION,
            Default.SCREENSAVER_DELAY,
            Default.THEME
        );
    }
}

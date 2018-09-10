package com.knobtviker.thermopile.data.sources.local.shared;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.MyObjectBox;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;

import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.DebugFlags;
import io.objectbox.android.AndroidObjectBrowser;

public class Database {

    public static BoxStore init(@NonNull Context context) {
        final BoxStoreBuilder builder = MyObjectBox.builder()
            .androidContext(context)
            .name(BuildConfig.DATABASE_NAME);

        if (BuildConfig.DEBUG_LOCAL) {
            builder
                .debugFlags(DebugFlags.LOG_TRANSACTIONS_READ | DebugFlags.LOG_TRANSACTIONS_WRITE)
                .debugRelations();
        }

        final BoxStore boxStore = builder.build();

        if (BuildConfig.DEBUG_LOCAL) {
            final boolean started = new AndroidObjectBrowser(boxStore).start(context);
        }

        if (boxStore.boxFor(Settings.class).count() == 0) {
            boxStore.boxFor(Settings.class).put(defaultSettings());
        }

        return boxStore;
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

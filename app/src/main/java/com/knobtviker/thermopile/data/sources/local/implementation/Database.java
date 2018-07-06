package com.knobtviker.thermopile.data.sources.local.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.MyObjectBox;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.utils.constants.ClockMode;
import com.knobtviker.thermopile.presentation.utils.constants.Default;
import com.knobtviker.thermopile.presentation.utils.constants.FormatDate;
import com.knobtviker.thermopile.presentation.utils.constants.FormatTime;
import com.knobtviker.thermopile.presentation.utils.constants.UnitAcceleration;
import com.knobtviker.thermopile.presentation.utils.constants.UnitPressure;
import com.knobtviker.thermopile.presentation.utils.constants.UnitTemperature;

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

    //TODO: Move this to some default integrity class
    public static Settings defaultSettings() {
        return new Settings(
            Default.TIMEZONE,
            ClockMode._24H,
            FormatDate.EEEE_DD_MM_YYYY,
            FormatTime.HH_MM,
            UnitTemperature.CELSIUS,
            UnitPressure.PASCAL,
            UnitAcceleration.METERS_PER_SECOND_2,
            Default.SCREENSAVER_DELAY,
            Default.THEME
        );
    }
}

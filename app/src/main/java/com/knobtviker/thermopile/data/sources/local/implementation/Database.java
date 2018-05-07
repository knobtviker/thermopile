package com.knobtviker.thermopile.data.sources.local.implementation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.MyObjectBox;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.ArrayList;
import java.util.List;

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

        if (INSTANCE.boxFor(PeripheralDevice.class).count() == 0) {
            INSTANCE.boxFor(PeripheralDevice.class).put(defaultPeripherals());
        }
        if (INSTANCE.boxFor(Settings.class).count() == 0) {
            INSTANCE.boxFor(Settings.class).put(defaultSettings());
        }
    }

    public static BoxStore getInstance() {
        return INSTANCE;
    }

    private static List<PeripheralDevice> defaultPeripherals() {
        final String bus = TextUtils.isEmpty(BoardDefaults.defaultI2CBus()) ? "" : BoardDefaults.defaultI2CBus();
        final List<PeripheralDevice> devices = new ArrayList<>(6);
        devices.add(new PeripheralDevice(0x77, bus, "Bosch", "BME280", true, true, true, false, false, false, false, false));
        devices.add(new PeripheralDevice(0x76, bus, "Bosch", "BME680", true, true, true, true, false, false, false, false));
        devices.add(new PeripheralDevice(0x68, bus, "Maxim Integrated Products", "DS3231", true, false, false, false, false, false, false, false));
        devices.add(new PeripheralDevice(0x39, bus, "TAOS", "TSL2561", false, false, false, false, true, false, false, false));
        devices.add(new PeripheralDevice(0x6B, bus, "STMicroelectronics", "LSM9DS1", true, false, false, false, false, true, true, false));
        devices.add(new PeripheralDevice(0x1E, bus, "STMicroelectronics", "LSM9DS1", false, false, false, false, false, false, false, true));
        return devices;
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

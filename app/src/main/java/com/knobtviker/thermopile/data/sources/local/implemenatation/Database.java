package com.knobtviker.thermopile.data.sources.local.implemenatation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.utils.ColorKit;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bojan on 30/01/2018.
 */

public class Database {

    //TODO: Enable encryption
    public static void init(@NonNull final Context context) {
        Realm.init(context);

        buildRealmOnDisk(context);
    }

    public static Realm getDefaultInstance() {
        return Realm.getDefaultInstance();
    }

    private static void buildRealmOnDisk(@NonNull final Context context) {
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
            .name(BuildConfig.DATABASE_NAME)
            .schemaVersion(BuildConfig.DATABASE_VERSION)
//            .deleteRealmIfMigrationNeeded()
//            .compactOnLaunch(CompactCallback.create())
            .initialData(realm -> {
                realm.insert(defaultSettings());
                realm.insert(defaultPeripherals());
                realm.insert(mockThresholds(context));
            })
            .build();

        Realm.setDefaultConfiguration(configuration);
    }

    private static Settings defaultSettings() {
        return new Settings(
            0L,
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

    private static ImmutableList<PeripheralDevice> defaultPeripherals() {
        final String bus = TextUtils.isEmpty(BoardDefaults.defaultI2CBus()) ? "" : BoardDefaults.defaultI2CBus();
        return ImmutableList.of(
            new PeripheralDevice(0x77, bus, "Bosch", "BME280", true, true, true, false, false, false, false, false),
            new PeripheralDevice(0x76, bus, "Bosch", "BME680", true, true, true, true, false, false, false, false),
            new PeripheralDevice(0x68, bus, "Maxim Integrated Products", "DS3231", true, false, false, false, false, false, false, false),
            new PeripheralDevice(0x39, bus, "TAOS", "TSL2561", false, false, false, false, true, false, false, false),
            new PeripheralDevice(0x6B, bus, "STMicroelectronics", "LSM9DS1", true, false, false, false, false, true, true, false),
            new PeripheralDevice(0x1E, bus, "STMicroelectronics", "LSM9DS1", false, false, false, false, false, false, false, true)
        );
    }

    private static ImmutableList<Threshold> mockThresholds(@NonNull final Context context) {
        final List<Threshold> mocks = new ArrayList<>(0);
        IntStream.range(0, 7)
            .forEach(
                day -> {
                    Threshold mockThreshold;
                    if (day == 0) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(10L);
                        mockThreshold.temperature(22);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.blue_500)));
                        mockThreshold.startHour(10);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(12);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);

                        mockThreshold = new Threshold();
                        mockThreshold.id(11L);
                        mockThreshold.temperature(25);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.red_500)));
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(22);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                    if (day == 1) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(20L);
                        mockThreshold.temperature(23);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.grey_500)));
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(11);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);

                        mockThreshold = new Threshold();
                        mockThreshold.id(21L);
                        mockThreshold.temperature(24);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.purple_500)));
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                    if (day == 2) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(30L);
                        mockThreshold.temperature(21);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.light_green_500)));
                        mockThreshold.startHour(12);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);

                        mockThreshold = new Threshold();
                        mockThreshold.id(31L);
                        mockThreshold.temperature(20);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.pink_500)));
                        mockThreshold.startHour(18);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                    if (day == 3) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(40L);
                        mockThreshold.temperature(27);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.light_blue_500)));
                        mockThreshold.startHour(8);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);

                        mockThreshold = new Threshold();
                        mockThreshold.id(41L);
                        mockThreshold.temperature(18);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.deep_orange_500)));
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(20);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                    if (day == 4) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(50L);
                        mockThreshold.temperature(20);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.teal_500)));
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);

                        mockThreshold = new Threshold();
                        mockThreshold.id(51L);
                        mockThreshold.temperature(22);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.amber_500)));
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                    if (day == 5) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(60L);
                        mockThreshold.temperature(23);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.indigo_500)));
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(20);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                    if (day == 6) {
                        mockThreshold = new Threshold();
                        mockThreshold.id(70L);
                        mockThreshold.temperature(24);
                        mockThreshold.color(ColorKit.colorToString(context.getColor(R.color.green_500)));
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(19);
                        mockThreshold.endMinute(0);
                        mockThreshold.day(day);

                        mocks.add(mockThreshold);
                    }
                }
            );
        return ImmutableList.copyOf(mocks);
    }
}

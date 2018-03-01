package com.knobtviker.thermopile.data.sources.local.implemenatation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
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

//    @Nullable
//    private static RealmConfiguration onDiskRealmConfig;

    //TODO: Enable encryption
    public static void init(@NonNull final Context context) {
        Realm.init(context);

//        buildRealmInMemory();
        buildRealmOnDisk();
    }

    public static Realm getDefaultInstance() {
        return Realm.getDefaultInstance();
    }

//    public static Realm getPersistentInstance() {
//        return Realm.getInstance(onDiskRealmConfig);
//    }

    private static void buildRealmInMemory() {
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
            .inMemory()
            .name(BuildConfig.MEMORY_DATABASE_NAME)
            .schemaVersion(BuildConfig.DATABASE_VERSION)
//            .deleteRealmIfMigrationNeeded()
//            .initialData(realm -> {
//                realm.insert(defaultSettings());
//                realm.insert(mockThresholds());
//            })
            .build();

        Realm.setDefaultConfiguration(configuration);
    }

    private static void buildRealmOnDisk() {
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
            .name(BuildConfig.DATABASE_NAME)
            .schemaVersion(BuildConfig.DATABASE_VERSION)
//            .deleteRealmIfMigrationNeeded()
            .initialData(realm -> {
                realm.insert(defaultSettings());
                realm.insert(mockThresholds());
            })
            .build();

        Realm.setDefaultConfiguration(configuration);
    }

    private static Settings defaultSettings() {
        final Settings settings = new Settings();

        settings.id(0L);
        settings.timezone(Constants.DEFAULT_TIMEZONE);
        settings.formatClock(Constants.CLOCK_MODE_24H);
        settings.unitTemperature(Constants.UNIT_TEMPERATURE_CELSIUS);
        settings.unitPressure(Constants.UNIT_PRESSURE_PASCAL);
        settings.unitMotion(Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2);
        settings.formatDate(Constants.DEFAULT_FORMAT_DATE);
        settings.formatTime(Constants.FORMAT_TIME_LONG_24H);

        return settings;
    }

    private static ImmutableList<Threshold> mockThresholds() {
        final List<Threshold> mocks = new ArrayList<>(0);
        IntStream.range(0, 7)
            .forEach(
                day -> {
                    final Threshold mockThreshold = new Threshold();
                    mockThreshold.day(day);
                    if (day == 0) {
                        mockThreshold.id(10L);
                        mockThreshold.color(R.color.blue_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(11L);
                        mockThreshold.color(R.color.red_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 1) {
                        mockThreshold.id(20L);
                        mockThreshold.color(R.color.grey_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(21L);
                        mockThreshold.color(R.color.purple_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 2) {
                        mockThreshold.id(30L);
                        mockThreshold.color(R.color.light_green_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(31L);
                        mockThreshold.color(R.color.pink_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 3) {
                        mockThreshold.id(40L);
                        mockThreshold.color(R.color.light_blue_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(41L);
                        mockThreshold.color(R.color.deep_orange_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 4) {
                        mockThreshold.id(50L);
                        mockThreshold.color(R.color.teal_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(51L);
                        mockThreshold.color(R.color.amber_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 5) {
                        mockThreshold.id(60L);
                        mockThreshold.color(R.color.indigo_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(20);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);
                    }
                    if (day == 6) {
                        mockThreshold.id(70L);
                        mockThreshold.color(R.color.green_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(19);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);
                    }
                }
            );
        return ImmutableList.copyOf(mocks);
    }
}

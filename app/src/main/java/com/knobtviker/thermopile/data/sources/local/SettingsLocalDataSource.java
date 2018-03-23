package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.SettingsDataSource;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 26/06/2017.
 */

public class SettingsLocalDataSource implements SettingsDataSource.Local {

    @Inject
    public SettingsLocalDataSource() {
    }

    @Override
    public RealmResults<Settings> load(@NonNull final Realm realm) {
        return realm
            .where(Settings.class)
            .findAll();
    }

    @Override
    public void saveTimezone(long settingsId, @NonNull String timezone) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.timezone(timezone);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public void saveClockMode(long settingsId, int clockMode) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.formatClock(clockMode);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public void saveFormatDate(long settingsId, @NonNull String item) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.formatDate(item);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public void saveFormatTime(long settingsId, @NonNull String item) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.formatTime(item);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public void saveTemperatureUnit(long settingsId, int unit) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.unitTemperature(unit);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public void savePressureUnit(long settingsId, int unit) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.unitPressure(unit);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public void saveTheme(long settingsId, int value) {
        final Realm realm = Realm.getDefaultInstance();

        final Settings settings = realm
            .where(Settings.class)
            .equalTo("id", settingsId)
            .findFirst();

        if (settings != null) {
            realm.beginTransaction();

            settings.theme(value);

            realm.insertOrUpdate(settings);
            realm.commitTransaction();
        }

        realm.close();
    }
}

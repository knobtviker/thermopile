package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;
import android.util.Log;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.SettingsDataSource;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 26/06/2017.
 */

public class SettingsLocalDataSource implements SettingsDataSource.Local {
    private final String TAG = SettingsLocalDataSource.class.getSimpleName();

    private static Optional<SettingsLocalDataSource> INSTANCE = Optional.empty();

    public static SettingsLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new SettingsLocalDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private SettingsLocalDataSource() {
    }

    @Override
    public RealmResults<Settings> load() {
        return Realm.getDefaultInstance()
            .where(Settings.class)
            .findAllAsync();
    }

    @Override
    public void saveTimezone(long settingsId, @NonNull String timezone) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
            realm1 -> {
                final Settings settings = realm1.where(Settings.class).equalTo("id", settingsId).findFirst();
                if (settings != null) {
                    settings.timezone(timezone);
                    realm1.insertOrUpdate(settings);
                }
            },
            error -> Log.e(TAG, error.getMessage(), error)
        );
        realm.close();
    }

    @Override
    public void saveClockMode(long settingsId, int clockMode) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
            realm1 -> {
                final Settings settings = realm1.where(Settings.class).equalTo("id", settingsId).findFirst();
                if (settings != null) {
                    settings.formatClock(clockMode);
                    realm1.insertOrUpdate(settings);
                }
            },
            error -> Log.e(TAG, error.getMessage(), error)
        );
        realm.close();
    }

    @Override
    public void saveFormatDate(long settingsId, @NonNull String item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
            realm1 -> {
                final Settings settings = realm1.where(Settings.class).equalTo("id", settingsId).findFirst();
                if (settings != null) {
                    settings.formatDate(item);
                    realm1.insertOrUpdate(settings);
                }
            },
            error -> Log.e(TAG, error.getMessage(), error)
        );
        realm.close();
    }

    @Override
    public void saveFormatTime(long settingsId, @NonNull String item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
            realm1 -> {
                final Settings settings = realm1.where(Settings.class).equalTo("id", settingsId).findFirst();
                if (settings != null) {
                    settings.formatTime(item);
                    realm1.insertOrUpdate(settings);
                }
            },
            error -> Log.e(TAG, error.getMessage(), error)
        );
        realm.close();
    }

    @Override
    public void saveTemperatureUnit(long settingsId, int unit) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
            realm1 -> {
                final Settings settings = realm1.where(Settings.class).equalTo("id", settingsId).findFirst();
                if (settings != null) {
                    settings.unitTemperature(unit);
                    realm1.insertOrUpdate(settings);
                }
            },
            error -> Log.e(TAG, error.getMessage(), error)
        );
        realm.close();
    }

    @Override
    public void savePressureUnit(long settingsId, int unit) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(
            realm1 -> {
                final Settings settings = realm1.where(Settings.class).equalTo("id", settingsId).findFirst();
                if (settings != null) {
                    settings.unitPressure(unit);
                    realm1.insertOrUpdate(settings);
                }
            },
            error -> Log.e(TAG, error.getMessage(), error)
        );
        realm.close();
    }
}

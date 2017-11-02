package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class SettingsRepository extends BaseRepository {

    private static Optional<SettingsRepository> INSTANCE = Optional.empty();

    private final SettingsLocalDataSource settingsLocalDataSource;

    public static SettingsRepository getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new SettingsRepository());
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            SettingsLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private SettingsRepository() {
        this.settingsLocalDataSource = SettingsLocalDataSource.getInstance();
    }

    public RealmResults<Settings> load() {
        return settingsLocalDataSource.load();
    }

    public void saveTimezone(final long settingsId, @NonNull final String timezone) {
        settingsLocalDataSource.saveTimezone(settingsId, timezone);
    }

    public void saveClockMode(final long settingsId, final int clockMode) {
        settingsLocalDataSource.saveClockMode(settingsId, clockMode);
    }

    public void saveFormatDate(final long settingsId, @NonNull final String item) {
        settingsLocalDataSource.saveFormatDate(settingsId, item);
    }

    public void saveFormatTime(final long settingsId, @NonNull final String item) {
        settingsLocalDataSource.saveFormatTime(settingsId, item);
    }

    public void saveTemperatureUnit(final long settingsId, final int unit) {
        settingsLocalDataSource.saveTemperatureUnit(settingsId, unit);
    }

    public void savePressureUnit(final long settingsId, final int unit) {
        settingsLocalDataSource.savePressureUnit(settingsId, unit);
    }
}

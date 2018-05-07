package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class SettingsRepository extends AbstractRepository {

    @Inject
    SettingsLocalDataSource settingsLocalDataSource;

    @Inject
    SettingsRepository() {
    }

    public Observable<List<Settings>> load() {
        return settingsLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public void saveTimezone(final long settingsId, @NonNull final String timezone) {
//        settingsLocalDataSource.saveTimezone(settingsId, timezone);
    }

    public void saveClockMode(final long settingsId, final int clockMode) {
//        settingsLocalDataSource.saveClockMode(settingsId, clockMode);
    }

    public void saveFormatDate(final long settingsId, @NonNull final String item) {
//        settingsLocalDataSource.saveFormatDate(settingsId, item);
    }

    public void saveFormatTime(final long settingsId, @NonNull final String item) {
//        settingsLocalDataSource.saveFormatTime(settingsId, item);
    }

    public void saveTemperatureUnit(final long settingsId, final int unit) {
//        settingsLocalDataSource.saveTemperatureUnit(settingsId, unit);
    }

    public void savePressureUnit(final long settingsId, final int unit) {
//        settingsLocalDataSource.savePressureUnit(settingsId, unit);
    }

    public void saveTheme(final long settingsId, final int value) {
//        settingsLocalDataSource.saveTheme(settingsId, value);
    }
}

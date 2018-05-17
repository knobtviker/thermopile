package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
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
            .observe()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Completable saveTimezone(final long settingsId, @NonNull final String timezone) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.timezone = timezone;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable saveClockMode(final long settingsId, final int clockMode) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.formatClock = clockMode;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable saveFormatDate(final long settingsId, @NonNull final String item) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.formatDate = item;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable saveFormatTime(final long settingsId, @NonNull final String item) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.formatTime = item;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable saveTemperatureUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.unitTemperature = unit;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable savePressureUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.unitPressure = unit;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable saveMotionUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.unitMotion = unit;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }

    public Completable saveTheme(final long settingsId, final int value) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .flatMap(settings -> {
                settings.theme = value;
                return settingsLocalDataSource.save(settings);
            })
            .ignoreElements();
    }
}

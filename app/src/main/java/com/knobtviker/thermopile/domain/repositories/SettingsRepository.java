package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
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

    public Flowable<Settings> observe() {
        return settingsLocalDataSource
            .observe()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Settings> load() {
        return settingsLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .map(items -> items.isEmpty() ? Database.defaultSettings() : items.get(0))
            .observeOn(schedulerProvider.io);
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
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.formatClock = clockMode;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveFormatDate(final long settingsId, @NonNull final String item) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.formatDate = item;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveFormatTime(final long settingsId, @NonNull final String item) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.formatTime = item;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveTemperatureUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.unitTemperature = unit;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable savePressureUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.unitPressure = unit;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveAccelerationUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.unitMotion = unit;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveTheme(final long settingsId, final int value) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.theme = value;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveScreensaverTimeout(long settingsId, int value) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulerProvider.io)
            .flatMap(settings -> {
                settings.screensaverDelay = value;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }
}

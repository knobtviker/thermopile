package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.shared.Database;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class SettingsRepository extends AbstractRepository {

    @NonNull
    private final SettingsLocalDataSource settingsLocalDataSource;

    @Inject
    public SettingsRepository(
        @NonNull final SettingsLocalDataSource settingsLocalDataSource,
        @NonNull final Schedulers schedulers
    ) {
        super(schedulers);
        this.settingsLocalDataSource = settingsLocalDataSource;
    }

    public Flowable<Settings> observe() {
        return settingsLocalDataSource
            .observe()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<Settings> load() {
        return settingsLocalDataSource
            .query()
            .subscribeOn(schedulers.trampoline)
            .map(items -> items.isEmpty() ? Database.defaultSettings() : items.get(0))
            .observeOn(schedulers.ui);
    }

    public Completable saveTimezone(final long settingsId, @NonNull final String timezone) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.timezone = timezone;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveClockMode(final long settingsId, final int clockMode) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.formatClock = clockMode;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveFormatDate(final long settingsId, @NonNull final String item) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.formatDate = item;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveFormatTime(final long settingsId, @NonNull final String item) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.formatTime = item;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveTemperatureUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.unitTemperature = unit;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable savePressureUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.unitPressure = unit;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveAccelerationUnit(final long settingsId, final int unit) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.unitMotion = unit;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveTheme(final long settingsId, final int value) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.theme = value;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }

    public Completable saveScreensaverTimeout(long settingsId, int value) {
        return settingsLocalDataSource
            .queryById(settingsId)
            .subscribeOn(schedulers.trampoline)
            .flatMap(settings -> {
                settings.screensaverDelay = value;
                return settingsLocalDataSource.save(settings);
            })
            .observeOn(schedulers.trampoline)
            .ignoreElements();
    }
}

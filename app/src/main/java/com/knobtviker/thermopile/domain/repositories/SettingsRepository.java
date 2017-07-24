package com.knobtviker.thermopile.domain.repositories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.converters.SettingsConverter;
import com.knobtviker.thermopile.data.models.local.SettingsTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Settings;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class SettingsRepository extends BaseRepository {

    private static Optional<SettingsRepository> INSTANCE = Optional.empty();

    private final SettingsLocalDataSource settingsLocalDataSource;
    private final SettingsConverter settingsConverter;

    public static SettingsRepository getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new SettingsRepository(context));
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            SettingsLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private SettingsRepository(@NonNull final Context context) {
        this.settingsLocalDataSource = SettingsLocalDataSource.getInstance(context);
        this.settingsConverter = new SettingsConverter();
    }

    public Observable<Settings> load() {
        return settingsLocalDataSource
            .load()
            .subscribeOn(schedulerProvider.io())
            .map(settingsConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Settings> save(@NonNull final SettingsTableEntity entity) {
        return settingsLocalDataSource
            .save(entity)
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(settingsConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<SettingsTableEntity> convertToLocal(@NonNull final Settings settings) {
        return Observable.defer(() -> Observable.just(settingsConverter.presentationToLocal(settings)));
    }
}

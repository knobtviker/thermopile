package com.knobtviker.thermopile.domain.repositories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.AtmosphereConverter;
import com.knobtviker.thermopile.data.models.local.AtmosphereTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.AtmosphereRawDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends BaseRepository {

    private static Optional<AtmosphereRepository> INSTANCE = Optional.empty();

    private final AtmosphereRawDataSource atmosphereRawDataSource;
    private final AtmosphereLocalDataSource atmosphereLocalDataSource;
    private final AtmosphereConverter atmosphereConverter;

    public static AtmosphereRepository getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereRepository(context));
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            AtmosphereRawDataSource.destroyInstance();
            AtmosphereLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private AtmosphereRepository(@NonNull final Context context) {
        this.atmosphereLocalDataSource = AtmosphereLocalDataSource.getInstance(context);
        this.atmosphereRawDataSource = AtmosphereRawDataSource.getInstance();
        this.atmosphereConverter = new AtmosphereConverter();
    }

    public Observable<AtmosphereTableEntity> read() {
        return atmosphereRawDataSource
            .read()
            .toObservable()
            .subscribeOn(schedulerProvider.sensors())
            .map(atmosphereConverter::rawToLocal)
            .observeOn(schedulerProvider.io());
    }

    public Observable<ImmutableList<Atmosphere>> load() {
        return atmosphereLocalDataSource
            .load()
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(ImmutableList::copyOf)
            .map(atmosphereConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Atmosphere> save(@NonNull final AtmosphereTableEntity entity) {
        return atmosphereLocalDataSource
            .save(entity)
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(atmosphereConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Atmosphere> last() {
        return atmosphereLocalDataSource
            .last()
            .subscribeOn(schedulerProvider.io())
            .map(atmosphereConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Float> luminosity() {
        return atmosphereRawDataSource
            .readLuminosity()
            .toObservable()
            .subscribeOn(schedulerProvider.sensors())
            .observeOn(schedulerProvider.ui());
    }
}

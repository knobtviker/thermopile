package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.converters.AtmosphereConverter;
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.AtmosphereRawDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends BaseRepository {

    private static Optional<AtmosphereRepository> INSTANCE = Optional.empty();

    private final AtmosphereRawDataSource atmosphereRawDataSource;
    private final AtmosphereLocalDataSource atmosphereLocalDataSource;
    private final AtmosphereConverter atmosphereConverter;

    public static AtmosphereRepository getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereRepository());
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

    private AtmosphereRepository() {
        this.atmosphereLocalDataSource = AtmosphereLocalDataSource.getInstance();
        this.atmosphereRawDataSource = AtmosphereRawDataSource.getInstance();
        this.atmosphereConverter = new AtmosphereConverter();
    }

    public Observable<Atmosphere> read() {
        return atmosphereRawDataSource
            .read()
            .toObservable()
            .subscribeOn(schedulerProvider.sensors())
            .map(atmosphereConverter::rawToLocal)
            .observeOn(schedulerProvider.sensors());
    }

    public RealmResults<Atmosphere> load() {
        return atmosphereLocalDataSource.load();
    }

    public void save(@NonNull final Atmosphere item) {
        atmosphereLocalDataSource.save(item);
    }

    public RealmResults<Atmosphere> latest() {
        return atmosphereLocalDataSource.latest();
    }

    public Observable<Float> luminosity() {
        return atmosphereRawDataSource
            .readLuminosity()
            .toObservable()
            .subscribeOn(schedulerProvider.sensors())
            .observeOn(schedulerProvider.sensors());
    }
}

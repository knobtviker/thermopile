package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends BaseRepository {

    private static Optional<AtmosphereRepository> INSTANCE = Optional.empty();

    private final AtmosphereLocalDataSource atmosphereLocalDataSource;

    public static AtmosphereRepository getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereRepository());
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            AtmosphereLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private AtmosphereRepository() {
        this.atmosphereLocalDataSource = AtmosphereLocalDataSource.getInstance();
    }

    public void save(@NonNull final Atmosphere item) {
        atmosphereLocalDataSource.save(item);
    }

    public RealmResults<Atmosphere> latest(@NonNull final Realm realm) {
        return atmosphereLocalDataSource.latest(realm);
    }
}

package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends BaseRepository {

    @Inject
    AtmosphereLocalDataSource atmosphereLocalDataSource;

    @Inject
    AtmosphereRepository() {
    }

    public void save(@NonNull final Atmosphere item) {
        atmosphereLocalDataSource.save(item);
    }

    public RealmResults<Atmosphere> latest(@NonNull final Realm realm) {
        return atmosphereLocalDataSource.latest(realm);
    }
}

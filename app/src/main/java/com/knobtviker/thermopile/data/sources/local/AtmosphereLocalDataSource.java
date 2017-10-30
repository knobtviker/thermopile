package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.sources.AtmosphereDataSource;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class AtmosphereLocalDataSource implements AtmosphereDataSource.Local {

    private static Optional<AtmosphereLocalDataSource> INSTANCE = Optional.empty();

    public static AtmosphereLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereLocalDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private AtmosphereLocalDataSource() {

    }

    @Override
    public RealmResults<Atmosphere> load() {
        return Realm
            .getDefaultInstance()
            .where(Atmosphere.class)
            .findAllAsync();
    }

    @Override
    public RealmResults<Atmosphere> latest() {
        return Realm
            .getDefaultInstance()
            .where(Atmosphere.class)
            .findAllSortedAsync("timestamp", Sort.DESCENDING);
    }

    @Override
    public void save(@NonNull Atmosphere item) {
        Realm
            .getDefaultInstance()
            .executeTransactionAsync(realm1 -> realm1.insertOrUpdate(item));
    }
}

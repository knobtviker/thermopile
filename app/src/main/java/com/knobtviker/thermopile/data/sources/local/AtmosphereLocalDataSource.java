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
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Atmosphere> results = realm
            .where(Atmosphere.class)
            .findAllAsync();
        realm.close();
        return results;
    }

    @Override
    public RealmResults<Atmosphere> last() {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Atmosphere> result = realm
            .where(Atmosphere.class)
            .findAllSortedAsync("timestamp", Sort.DESCENDING);
        realm.close();
        return result;
    }

    @Override
    public void save(@NonNull Atmosphere item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(item));
        realm.close();
    }
}

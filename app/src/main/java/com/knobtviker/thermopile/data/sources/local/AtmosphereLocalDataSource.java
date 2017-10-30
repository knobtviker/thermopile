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

    private static Realm realm = null;

    public static AtmosphereLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereLocalDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            if (realm != null && !realm.isClosed()) {
                realm.close();
                realm = null;
            }
            INSTANCE = Optional.empty();
        }
    }

    private AtmosphereLocalDataSource() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public RealmResults<Atmosphere> load() {
        return realm
            .where(Atmosphere.class)
            .findAllAsync();
    }

    @Override
    public RealmResults<Atmosphere> latest() {
        return realm
            .where(Atmosphere.class)
            .findAllSortedAsync("timestamp", Sort.DESCENDING);
    }

    @Override
    public void save(@NonNull Atmosphere item) {
//        Log.i("ATMOSPHERE SAVE", item.timestamp() + " --- " + item.temperature());
        realm
            .executeTransactionAsync(realm1 -> realm1.insertOrUpdate(item));
    }
}

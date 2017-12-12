package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.sources.AtmosphereDataSource;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class AtmosphereLocalDataSource implements AtmosphereDataSource.Local {

    @Inject
    public AtmosphereLocalDataSource() {
    }

    @Override
    public RealmResults<Atmosphere> latest(@NonNull final Realm realm) {
        return realm
            .where(Atmosphere.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull Atmosphere item) {
//        Log.i("ATMOSPHERE SAVE", item.timestamp() + " --- " + item.temperature()+" --- "+item.humidity()+" --- "+item.pressure());
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }
}

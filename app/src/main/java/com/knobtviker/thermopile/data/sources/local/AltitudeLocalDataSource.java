package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.sources.AltitudeDataSource;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class AltitudeLocalDataSource implements AltitudeDataSource.Local {

    @Inject
    public AltitudeLocalDataSource() {
    }

    @Override
    public RealmResults<Altitude> latest(@NonNull final Realm realm) {
        return realm
            .where(Altitude.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull final Altitude item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(item));
        realm.close();
    }
}

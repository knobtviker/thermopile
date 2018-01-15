package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.sources.HumidityDataSource;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class HumidityLocalDataSource implements HumidityDataSource.Local {

    @Inject
    public HumidityLocalDataSource() {
    }

    @Override
    public RealmResults<Humidity> latest(@NonNull final Realm realm) {
        return realm
            .where(Humidity.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull final Humidity item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(item));
        realm.close();
    }
}

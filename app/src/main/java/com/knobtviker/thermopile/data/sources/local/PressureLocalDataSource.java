package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.sources.PressureDataSource;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class PressureLocalDataSource implements PressureDataSource.Local {

    @Inject
    public PressureLocalDataSource() {
    }

    @Override
    public RealmResults<Pressure> latest(@NonNull final Realm realm) {
        return realm
            .where(Pressure.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull List<Pressure> items) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(items);
        realm.commitTransaction();
        realm.close();
    }
}

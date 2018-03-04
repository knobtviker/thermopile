package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.ThresholdDataSource;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class ThresholdLocalDataSource implements ThresholdDataSource.Local {

    @Inject
    public ThresholdLocalDataSource() {
    }

    @Override
    public RealmResults<Threshold> load(@NonNull final Realm realm) {
        final String[] fieldNames = {"day", "startHour", "startMinute"};
        final Sort[] directions = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};

        return realm
            .where(Threshold.class)
            .sort(fieldNames, directions)
            .findAll();
    }

    @Override
    public RealmResults<Threshold> loadByDay(@NonNull final Realm realm, int day) {
        final String[] fieldNames = {"startHour", "startMinute"};
        final Sort[] directions = {Sort.ASCENDING, Sort.ASCENDING};

        return realm
            .where(Threshold.class)
            .equalTo("day", day)
            .sort(fieldNames, directions)
            .findAll();
    }

    @Override
    public RealmResults<Threshold> loadById(@NonNull final Realm realm, long thresholdId) {
        return realm
            .where(Threshold.class)
            .equalTo("id", thresholdId)
            .findAll();
    }

    @Override
    public void save(@NonNull Threshold item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }
}

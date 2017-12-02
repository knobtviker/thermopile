package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.ThresholdDataSource;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class ThresholdLocalDataSource implements ThresholdDataSource.Local {

    private static Optional<ThresholdLocalDataSource> INSTANCE = Optional.empty();

    public static ThresholdLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ThresholdLocalDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ThresholdLocalDataSource() {
    }

    @Override
    public RealmResults<Threshold> load(@NonNull final Realm realm) {
        return realm
            .where(Threshold.class)
            .findAll();
    }

    @Override
    public RealmResults<Threshold> loadByDay(@NonNull final Realm realm, int day) {
        final String[] fieldNames = {"startHour", "startMinute"};
        final Sort[] directions = {Sort.ASCENDING, Sort.ASCENDING};

        return realm
            .where(Threshold.class)
            .equalTo("day", day)
            .findAllSorted(fieldNames, directions);
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

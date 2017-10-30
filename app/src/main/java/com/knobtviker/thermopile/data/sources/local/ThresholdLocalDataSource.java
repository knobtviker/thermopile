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

    private static Realm realm = null;

    public static ThresholdLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ThresholdLocalDataSource());
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

    private ThresholdLocalDataSource() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public RealmResults<Threshold> load() {
        return realm
            .where(Threshold.class)
            .findAllAsync();
    }

    @Override
    public RealmResults<Threshold> loadByDay(int day) {
        final String[] fieldNames = {"startHour", "startMinute"};
        final Sort[] directions = {Sort.ASCENDING, Sort.ASCENDING};

        return realm
            .where(Threshold.class)
            .equalTo("day", day)
            .findAllSortedAsync(fieldNames, directions);
    }

    @Override
    public Threshold loadById(long thresholdId) {
        return realm
            .where(Threshold.class)
            .equalTo("id", thresholdId)
            .findAllAsync()
            .first();
    }

    @Override
    public void save(@NonNull Threshold item) {
        realm
            .executeTransactionAsync(realm1 -> {
                realm1.insertOrUpdate(item);
            });
    }
}

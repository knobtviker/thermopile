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
    public RealmResults<Threshold> load() {
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Threshold> results = realm
            .where(Threshold.class)
            .findAllAsync();
        realm.close();
        return results;
    }

    @Override
    public RealmResults<Threshold> loadByDay(int day) {
        final String[] fieldNames = {"startHour", "startMinute"};
        final Sort[] directions = {Sort.ASCENDING, Sort.ASCENDING};

        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<Threshold> results = realm
            .where(Threshold.class)
            .equalTo("day", day)
            .findAllSortedAsync(fieldNames, directions);
        realm.close();
        return results;
    }

    @Override
    public void save(@NonNull Threshold item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            final Number maxValue = realm1.where(Threshold.class).max("id");
            final long newId = (maxValue != null) ? maxValue.longValue() + 1L : 0L;
            item.id(newId);

            realm1.insertOrUpdate(item);
        });
        realm.close();
    }
}

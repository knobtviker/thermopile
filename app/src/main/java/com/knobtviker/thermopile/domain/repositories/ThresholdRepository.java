package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class ThresholdRepository extends BaseRepository {

    private static Optional<ThresholdRepository> INSTANCE = Optional.empty();

    private final ThresholdLocalDataSource thresholdLocalDataSource;

    public static ThresholdRepository getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ThresholdRepository());
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            ThresholdLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private ThresholdRepository() {
        this.thresholdLocalDataSource = ThresholdLocalDataSource.getInstance();
    }

    public RealmResults<Threshold> load(@NonNull final Realm realm) {
        return thresholdLocalDataSource.load(realm);
    }

    public RealmResults<Threshold> loadByDay(@NonNull final Realm realm, final int day) {
        return thresholdLocalDataSource.loadByDay(realm, day);
    }

    public RealmResults<Threshold> loadById(@NonNull final Realm realm, final long thresholdId) {
        return thresholdLocalDataSource.loadById(realm, thresholdId);
    }

    public void save(@NonNull final Threshold item) {
        thresholdLocalDataSource.save(item);
    }
}

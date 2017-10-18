package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

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

    public RealmResults<Threshold> load() {
        return thresholdLocalDataSource.load();
    }

    public RealmResults<Threshold> loadByDay(final int day) {
        return thresholdLocalDataSource.loadByDay(day);
    }

    public void save(@NonNull final Threshold item) {
        thresholdLocalDataSource.save(item);
    }
}

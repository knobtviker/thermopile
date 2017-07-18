package com.knobtviker.thermopile.data.sources.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.ThresholdTableEntity;
import com.knobtviker.thermopile.data.sources.ThresholdDataSource;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;

import java.util.List;
import java.util.Optional;

import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Created by bojan on 26/06/2017.
 */

public class ThresholdLocalDataSource implements ThresholdDataSource.Local {

    private static Optional<ThresholdLocalDataSource> INSTANCE = Optional.empty();

    private final ReactiveEntityStore<Persistable> database;

    public static ThresholdLocalDataSource getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ThresholdLocalDataSource(context));
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ThresholdLocalDataSource(@NonNull final Context context) {
        this.database = Database.getInstance(context).database();
    }

    @Override
    public Single<List<ThresholdTableEntity>> load() {
        return database
            .select(ThresholdTableEntity.class)
            .get()
            .observable()
            .toList();
    }

    @Override
    public Single<ThresholdTableEntity> save(@NonNull ThresholdTableEntity item) {
        return database.upsert(item);
    }
}

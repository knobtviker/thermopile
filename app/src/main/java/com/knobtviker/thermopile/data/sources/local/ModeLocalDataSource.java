package com.knobtviker.thermopile.data.sources.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.ModeTableEntity;
import com.knobtviker.thermopile.data.sources.ModeDataSource;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;

import java.util.List;
import java.util.Optional;

import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Created by bojan on 26/06/2017.
 */

public class ModeLocalDataSource implements ModeDataSource.Local {

    private static Optional<ModeLocalDataSource> INSTANCE = Optional.empty();

    private final ReactiveEntityStore<Persistable> database;

    public static ModeLocalDataSource getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ModeLocalDataSource(context));
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ModeLocalDataSource(@NonNull final Context context) {
        this.database = Database.getInstance(context).database();
    }

    @Override
    public Single<List<ModeTableEntity>> load() {
        return database
            .select(ModeTableEntity.class)
            .get()
            .observable()
            .toList();
    }

    @Override
    public Single<ModeTableEntity> save(@NonNull ModeTableEntity item) {
        return database.upsert(item);
    }
}

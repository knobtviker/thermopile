package com.knobtviker.thermopile.data.sources.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.ReadingTableEntity;
import com.knobtviker.thermopile.data.sources.ReadingDataSource;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;

import java.util.List;
import java.util.Optional;

import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Created by bojan on 26/06/2017.
 */

public class ReadingLocalDataSource implements ReadingDataSource.Local {

    private static Optional<ReadingLocalDataSource> INSTANCE = Optional.empty();

    private final ReactiveEntityStore<Persistable> database;

    public static ReadingLocalDataSource getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ReadingLocalDataSource(context));
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ReadingLocalDataSource(@NonNull final Context context) {
        this.database = Database.getInstance(context).database();
    }

    @Override
    public Single<List<ReadingTableEntity>> load() {
        return database
            .select(ReadingTableEntity.class)
            .get()
            .observable()
            .toList();
    }

    @Override
    public Single<ReadingTableEntity> save(@NonNull ReadingTableEntity item) {
        return database.upsert(item);
    }
}

package com.knobtviker.thermopile.data.sources.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.AtmosphereTableEntity;
import com.knobtviker.thermopile.data.sources.AtmosphereDataSource;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;

import java.util.List;
import java.util.Optional;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Created by bojan on 26/06/2017.
 */

public class AtmosphereLocalDataSource implements AtmosphereDataSource.Local {

    private static Optional<AtmosphereLocalDataSource> INSTANCE = Optional.empty();

    private final ReactiveEntityStore<Persistable> database;

    public static AtmosphereLocalDataSource getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereLocalDataSource(context));
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private AtmosphereLocalDataSource(@NonNull final Context context) {
        this.database = Database.getInstance(context).database();
    }

    @Override
    public Single<List<AtmosphereTableEntity>> load() {
        return database
            .select(AtmosphereTableEntity.class)
            .get()
            .observable()
            .toList();
    }

    @Override
    public Single<AtmosphereTableEntity> save(@NonNull AtmosphereTableEntity item) {
        return database.upsert(item);
    }

    @Override
    public Observable<AtmosphereTableEntity> last() {
        return database
            .select(AtmosphereTableEntity.class)
            .orderBy(AtmosphereTableEntity.TIMESTAMP.desc())
            .limit(1)
            .get()
            .observable();
    }
}

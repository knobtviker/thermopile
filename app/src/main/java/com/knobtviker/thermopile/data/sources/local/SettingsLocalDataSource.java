package com.knobtviker.thermopile.data.sources.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.SettingsTableEntity;
import com.knobtviker.thermopile.data.sources.SettingsDataSource;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;

import java.util.Optional;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Created by bojan on 26/06/2017.
 */

public class SettingsLocalDataSource implements SettingsDataSource.Local {

    private static Optional<SettingsLocalDataSource> INSTANCE = Optional.empty();

    private final ReactiveEntityStore<Persistable> database;

    public static SettingsLocalDataSource getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new SettingsLocalDataSource(context));
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private SettingsLocalDataSource(@NonNull final Context context) {
        this.database = Database.getInstance(context).database();
    }

    @Override
    public Observable<SettingsTableEntity> load() {
        return database
            .select(SettingsTableEntity.class)
            .limit(1)
            .get()
            .observable();
    }

    @Override
    public Single<SettingsTableEntity> save(@NonNull SettingsTableEntity item) {
        return database.upsert(item);
    }
}

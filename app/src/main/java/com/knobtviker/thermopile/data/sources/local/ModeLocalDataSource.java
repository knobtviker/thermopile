package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Mode;
import com.knobtviker.thermopile.data.sources.ModeDataSource;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 26/06/2017.
 */

public class ModeLocalDataSource implements ModeDataSource.Local {

    private static Optional<ModeLocalDataSource> INSTANCE = Optional.empty();

    public static ModeLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ModeLocalDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ModeLocalDataSource() {
    }

    @Override
    public RealmResults<Mode> load() {
        return Realm
            .getDefaultInstance()
            .where(Mode.class)
            .findAllAsync();
    }

    @Override
    public void save(@NonNull Mode item) {
        Realm
            .getDefaultInstance()
            .executeTransactionAsync(realm1 -> realm1.insertOrUpdate(item));
    }
}

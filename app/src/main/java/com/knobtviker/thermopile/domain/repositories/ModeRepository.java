package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Mode;
import com.knobtviker.thermopile.data.sources.local.ModeLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class ModeRepository extends BaseRepository {

    private static Optional<ModeRepository> INSTANCE = Optional.empty();

    private final ModeLocalDataSource modeLocalDataSource;

    public static ModeRepository getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ModeRepository());
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            ModeLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private ModeRepository() {
        this.modeLocalDataSource = ModeLocalDataSource.getInstance();
    }

    public RealmResults<Mode> load() {
        return modeLocalDataSource.load();
    }

    public void save(@NonNull final Mode item) {
        modeLocalDataSource.save(item);
    }
}

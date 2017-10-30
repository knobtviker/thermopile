package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.SettingsDataSource;

import java.util.Optional;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 26/06/2017.
 */

public class SettingsLocalDataSource implements SettingsDataSource.Local {

    private static Optional<SettingsLocalDataSource> INSTANCE = Optional.empty();

    private static Realm realm = null;

    public static SettingsLocalDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new SettingsLocalDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            if (realm != null && !realm.isClosed()) {
                realm.close();
                realm = null;
            }
            INSTANCE = Optional.empty();
        }
    }

    private SettingsLocalDataSource() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public RealmResults<Settings> load() {
        return realm
            .where(Settings.class)
            .findAllAsync();
    }

    @Override
    public void save(@NonNull Settings item) {
        realm
            .executeTransactionAsync(realm1 -> realm1.insertOrUpdate(item));
    }
}

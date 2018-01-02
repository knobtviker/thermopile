package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.sources.AtmosphereDataSource;

import javax.inject.Inject;

import io.realm.Realm;

/**
 * Created by bojan on 26/06/2017.
 */

public class AtmosphereLocalDataSource implements AtmosphereDataSource.Local {

    @Inject
    public AtmosphereLocalDataSource() {
    }

    @Override
    public void save(@NonNull final Atmosphere item) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realm1 -> realm1.insert(item));
        realm.close();
    }
}

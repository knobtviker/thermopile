package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface AtmosphereDataSource {

    interface Local {

        RealmResults<Atmosphere> latest(@NonNull final Realm realm);

        void save(@NonNull final Atmosphere item);
    }
}

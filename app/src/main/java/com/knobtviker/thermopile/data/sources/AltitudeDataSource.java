package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Altitude;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface AltitudeDataSource {

    interface Local {

        RealmResults<Altitude> latest(@NonNull final Realm realm);

        void save(@NonNull final Altitude item);
    }
}

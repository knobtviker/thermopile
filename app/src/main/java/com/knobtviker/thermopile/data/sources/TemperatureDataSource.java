package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Temperature;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface TemperatureDataSource {

    interface Local {

        RealmResults<Temperature> latest(@NonNull final Realm realm);

        void save(@NonNull final Temperature item);
    }
}

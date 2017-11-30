package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface ThresholdDataSource {

    interface Local {

        RealmResults<Threshold> load(@NonNull final Realm realm);

        RealmResults<Threshold> loadByDay(@NonNull final Realm realm, final int day);

        Threshold loadById(@NonNull final Realm realm, final long thresholdId);

        void save(@NonNull final Threshold item);
    }
}

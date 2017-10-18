package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;

import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface ThresholdDataSource {

    interface Local {

        RealmResults<Threshold> load();

        RealmResults<Threshold> loadByDay(final int day);

        void save(@NonNull final Threshold item);
    }
}

package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.AirQuality;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface AirQualityDataSource {

    interface Local {

        RealmResults<AirQuality> latest(@NonNull final Realm realm);

        void save(@NonNull final AirQuality item);
    }
}

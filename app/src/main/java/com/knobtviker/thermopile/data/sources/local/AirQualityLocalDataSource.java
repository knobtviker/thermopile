package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;
import android.util.Log;

import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.sources.AirQualityDataSource;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class AirQualityLocalDataSource implements AirQualityDataSource.Local {

    @Inject
    public AirQualityLocalDataSource() {
    }

    @Override
    public RealmResults<AirQuality> load(@NonNull final Realm realm) {
        Log.w("BOJAN", "Loading all eventually results in out of memory.");
        return realm
            .where(AirQuality.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull List<AirQuality> items) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(items));
        realm.close();
    }
}

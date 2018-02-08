package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;
import android.util.Log;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.sources.HumidityDataSource;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class HumidityLocalDataSource implements HumidityDataSource.Local {

    @Inject
    public HumidityLocalDataSource() {
    }

    @Override
    public RealmResults<Humidity> load(@NonNull final Realm realm) {
        Log.w("BOJAN", "Loading all eventually results in out of memory.");
        return realm
            .where(Humidity.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull List<Humidity> items) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(items));
        realm.close();
    }
}

package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.sources.AltitudeDataSource;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class AltitudeLocalDataSource implements AltitudeDataSource.Local {

    @Inject
    public AltitudeLocalDataSource() {
    }

    @Override
    public RealmResults<Altitude> latest(@NonNull final Realm realm) {
        return realm
            .where(Altitude.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull List<Altitude> items) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(items));
        realm.close();
    }
}

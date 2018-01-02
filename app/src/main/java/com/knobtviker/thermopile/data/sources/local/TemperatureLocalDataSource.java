package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.TemperatureDataSource;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by bojan on 26/06/2017.
 */

public class TemperatureLocalDataSource implements TemperatureDataSource.Local {

    @Inject
    public TemperatureLocalDataSource() {
    }

    @Override
    public RealmResults<Temperature> latest(@NonNull final Realm realm) {
        return realm
            .where(Temperature.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull final Realm realm, @NonNull final List<Temperature> items) {
        realm.beginTransaction();
        realm.insertOrUpdate(items);
        realm.commitTransaction();
//        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(items));
//        return Completable.create(emitter -> {
//            if (!emitter.isDisposed()) {
//                try {
//                    realm.executeTransaction(realm1 -> realm1.insertOrUpdate(items));
//                    emitter.onComplete();
//                } catch (Exception e) {
//                    emitter.onError(e);
//                }
//            }
//        });
    }
}

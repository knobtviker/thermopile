package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.sources.HumidityDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
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
    public RealmResults<Humidity> latest(@NonNull final Realm realm) {
        return realm
            .where(Humidity.class)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public Completable save(@NonNull final Realm realm, @NonNull final List<Humidity> items) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    realm.executeTransaction(realm1 -> realm1.insertOrUpdate(items));
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}

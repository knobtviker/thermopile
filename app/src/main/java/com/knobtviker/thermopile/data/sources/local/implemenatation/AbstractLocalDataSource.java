package com.knobtviker.thermopile.data.sources.local.implemenatation;

import android.util.Log;

import com.knobtviker.thermopile.data.models.local.implementation.SensorModel;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class AbstractLocalDataSource<T extends SensorModel> implements BaseLocalDataSource<T> {
    private final String TAG = AbstractLocalDataSource.class.getSimpleName();

    private final Class<T> clazz;

    public AbstractLocalDataSource(@NonNull final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public RealmResults<T> load(@NonNull final Realm realm) {
        Log.w(TAG, "Loading all eventually results in out of memory.");
        return realm
            .where(clazz)
            .sort("timestamp", Sort.DESCENDING)
            .findAll();
    }

    @Override
    public void save(@NonNull final List<T> items) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(items));
        realm.close();
    }

    @Override
    public void save(@NonNull final T item) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insert(item));
        realm.close();
    }
}

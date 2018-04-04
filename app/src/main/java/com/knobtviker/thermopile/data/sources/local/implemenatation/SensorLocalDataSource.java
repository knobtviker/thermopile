package com.knobtviker.thermopile.data.sources.local.implemenatation;

import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class SensorLocalDataSource<T extends RealmModel> extends AbstractLocalDataSource<T>  {
    private final String TAG = SensorLocalDataSource.class.getSimpleName();

    public SensorLocalDataSource(@NonNull final Class<T> clazz) {
        super(clazz);
    }

    @Override
    public RealmResults<T> load(@NonNull final Realm realm) {
        Log.w(TAG, "Loading all eventually results in out of memory.");
        return loadSorted(realm,"timestamp", Sort.DESCENDING);
    }
}

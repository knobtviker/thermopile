package com.knobtviker.thermopile.data.sources.local.implemenatation;

import com.knobtviker.thermopile.data.models.local.implementation.DeviceModel;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmResults;

public abstract class AbstractReadOnlyLocalDataSource<T extends DeviceModel> implements BaseReadOnlyLocalDataSource<T> {
    private final String TAG = AbstractReadOnlyLocalDataSource.class.getSimpleName();

    private final Class<T> clazz;

    public AbstractReadOnlyLocalDataSource(@NonNull final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public RealmResults<T> load(@NonNull final Realm realm) {
        return
            realm
                .where(clazz)
                .findAll();
    }

    @Override
    public void save(List<Integer> items) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            final RealmResults<T> results = realm1
                .where(clazz)
                .in("address", items.toArray(new Integer[items.size()]))
                .findAll();

            results.forEach(item -> item.enabled(true));

            realm1.insertOrUpdate(results);
        });

        realm.close();
    }
}

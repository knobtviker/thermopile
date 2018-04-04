package com.knobtviker.thermopile.data.sources.local.implemenatation;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class AbstractLocalDataSource<T extends RealmModel> implements BaseLocalDataSource<T> {

    private final Class<T> clazz;

    AbstractLocalDataSource(@NonNull final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public RealmResults<T> load(@NonNull final Realm realm) {
        return realm
                .where(clazz)
                .findAll();
    }

    @Override
    public RealmResults<T> loadSorted(Realm realm, String fieldName, Sort sortOrder) {
        return realm
            .where(clazz)
            .sort(fieldName, sortOrder)
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

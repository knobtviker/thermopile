package com.knobtviker.thermopile.data.sources.local.implemenatation;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;

public interface BaseLocalDataSource<T extends RealmModel> {

    RealmResults<T> load(@NonNull final Realm realm);

    T loadById(@NonNull Realm realm, @NonNull final String fieldName, @NonNull final String id);

    RealmResults<T> loadSorted(@NonNull final Realm realm, @NonNull final String fieldName, @NonNull final Sort sortOrder);

    void save(@NonNull final List<T> items);

    void save(@NonNull final T item);
}

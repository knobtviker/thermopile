package com.knobtviker.thermopile.data.sources.local.implemenatation;

import com.knobtviker.thermopile.data.models.local.implementation.SensorModel;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmResults;

public interface BaseLocalDataSource<T extends SensorModel> {

    RealmResults<T> load(@NonNull final Realm realm);

    void save(@NonNull final List<T> items);

    void save(@NonNull final T item);
}

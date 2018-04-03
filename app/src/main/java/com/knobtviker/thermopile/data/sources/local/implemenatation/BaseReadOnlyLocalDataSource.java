package com.knobtviker.thermopile.data.sources.local.implemenatation;

import com.knobtviker.thermopile.data.models.local.implementation.DeviceModel;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmResults;

public interface BaseReadOnlyLocalDataSource<T extends DeviceModel> {

    RealmResults<T> load(@NonNull final Realm realm);

    void save(@NonNull final List<Integer> items);
}

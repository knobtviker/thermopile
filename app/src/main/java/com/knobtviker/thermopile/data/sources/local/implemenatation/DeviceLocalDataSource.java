package com.knobtviker.thermopile.data.sources.local.implemenatation;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmModel;

public abstract class DeviceLocalDataSource<T extends RealmModel> extends AbstractLocalDataSource<T> {

    public DeviceLocalDataSource(@NonNull final Class<T> clazz) {
        super(clazz);
    }

    public T loadById(@NonNull Realm realm, String id) {
        return super.loadById(realm, "primaryKey", id);
    }
}

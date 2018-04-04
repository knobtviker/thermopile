package com.knobtviker.thermopile.data.sources.local.implemenatation;

import io.reactivex.annotations.NonNull;
import io.realm.RealmModel;

public abstract class DeviceLocalDataSource<T extends RealmModel> extends AbstractLocalDataSource<T> {

    public DeviceLocalDataSource(@NonNull final Class<T> clazz) {
        super(clazz);
    }
}

package com.knobtviker.thermopile.data.models.local.implementation;

import io.reactivex.annotations.NonNull;
import io.realm.RealmModel;

public interface SensorValue extends RealmModel {

    long timestamp();

    void timestamp(final long timestamp);

    int accuracy();

    void accuracy(final int accuracy);

    String vendor();

    void vendor(@NonNull final String vendor);

    String name();

    void name(@NonNull final String name);
}

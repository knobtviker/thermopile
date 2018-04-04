package com.knobtviker.thermopile.data.models.local.implementation;

import io.reactivex.annotations.NonNull;
import io.realm.RealmModel;

public interface DeviceModel extends RealmModel {

    String vendor();

    void vendor(@NonNull final String vendor);

    String name();

    void name(@NonNull final String name);

    String bus();

    void bus(@NonNull final String bus);

    int address();

    void address(final int address);

//    boolean enabled();
//
//    void enabled(final boolean enabled);

    boolean connected();

    void connected(final boolean connected);
}

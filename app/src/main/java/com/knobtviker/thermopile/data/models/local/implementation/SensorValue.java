package com.knobtviker.thermopile.data.models.local.implementation;

import io.realm.RealmModel;

public interface SensorValue extends RealmModel {

    long timestamp();

    void timestamp(final long timestamp);
}

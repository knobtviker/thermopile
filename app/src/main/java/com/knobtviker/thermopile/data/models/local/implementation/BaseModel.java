package com.knobtviker.thermopile.data.models.local.implementation;

import io.realm.RealmModel;

public interface BaseModel extends RealmModel {

    long id();

    void id(final long id);
}

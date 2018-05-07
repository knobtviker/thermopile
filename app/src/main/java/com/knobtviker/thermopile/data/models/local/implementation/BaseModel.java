package com.knobtviker.thermopile.data.models.local.implementation;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;

@BaseEntity
public abstract class BaseModel {

    @Id
    public long id;
}

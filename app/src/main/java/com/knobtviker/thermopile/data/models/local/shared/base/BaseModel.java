package com.knobtviker.thermopile.data.models.local.shared.base;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;

@BaseEntity
public abstract class BaseModel {

    @Id
    public long id;
}

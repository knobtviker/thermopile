package com.knobtviker.thermopile.data.models.local.shared.base;

import io.objectbox.annotation.BaseEntity;

@BaseEntity
public abstract class SensorModel extends BaseModel {

    public long timestamp;

    public String vendor;

    public String name;

}

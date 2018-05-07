package com.knobtviker.thermopile.data.models.local.implementation;

import io.objectbox.annotation.BaseEntity;

@BaseEntity
public abstract class SingleModel extends SensorModel {

    public float value;
}

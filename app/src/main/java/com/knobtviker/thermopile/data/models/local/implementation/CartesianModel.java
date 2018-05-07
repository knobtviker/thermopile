package com.knobtviker.thermopile.data.models.local.implementation;

import io.objectbox.annotation.BaseEntity;

@BaseEntity
public abstract class CartesianModel extends SensorModel {

    public float valueX;

    public float valueY;

    public float valueZ;
}

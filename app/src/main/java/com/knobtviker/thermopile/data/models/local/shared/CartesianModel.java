package com.knobtviker.thermopile.data.models.local.shared;

import com.knobtviker.thermopile.data.models.local.shared.base.SensorModel;

import io.objectbox.annotation.BaseEntity;

@BaseEntity
public abstract class CartesianModel extends SensorModel {

    public float valueX;

    public float valueY;

    public float valueZ;
}

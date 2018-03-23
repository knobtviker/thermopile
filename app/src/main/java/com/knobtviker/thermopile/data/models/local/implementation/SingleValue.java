package com.knobtviker.thermopile.data.models.local.implementation;

public interface SingleValue extends SensorModel {

    float value();

    void value(final float value);
}

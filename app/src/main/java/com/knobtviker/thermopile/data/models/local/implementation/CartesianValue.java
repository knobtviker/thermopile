package com.knobtviker.thermopile.data.models.local.implementation;

public interface CartesianValue extends SensorModel {

    float valueX();

    void valueX(final float valueX);

    float valueY();

    void valueY(final float valueY);

    float valueZ();

    void valueZ(final float valueZ);
}

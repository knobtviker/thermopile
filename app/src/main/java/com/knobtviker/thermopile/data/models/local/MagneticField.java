package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.CartesianValue;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by bojan on 27/12/2017.
 */

public class MagneticField extends RealmObject implements CartesianValue {

    public MagneticField(final long timestamp, final float valueX, final float valueY, final float valueZ) {
        timestamp(timestamp);
        valueX(valueX);
        valueY(valueY);
        valueZ(valueZ);
    }

    public MagneticField(){}

    @Index
    private long timestamp;

    private float valueX;

    private float valueY;

    private float valueZ;

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public float valueX() {
        return valueX;
    }

    @Override
    public float valueY() {
        return valueY;
    }

    @Override
    public float valueZ() {
        return valueZ;
    }

    @Override
    public void valueX(float valueX) {
        this.valueX = valueX;
    }

    @Override
    public void valueY(float valueY) {
        this.valueY = valueY;
    }

    @Override
    public void valueZ(float valueZ) {
        this.valueZ = valueZ;
    }
}

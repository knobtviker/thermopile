package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.CartesianValue;

import io.reactivex.annotations.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by bojan on 27/12/2017.
 */

public class AngularVelocity extends RealmObject implements CartesianValue {

    public AngularVelocity(final long timestamp, final float valueX, final float valueY, final float valueZ, final int accuracy, @NonNull final String vendor, @NonNull final String name) {
        timestamp(timestamp);
        valueX(valueX);
        valueY(valueY);
        valueZ(valueZ);
        accuracy(accuracy);
        vendor(vendor);
        name(name);
    }

    public AngularVelocity(){}

    @Index
    private long timestamp;

    private float valueX;

    private float valueY;

    private float valueZ;

    private int accuracy;

    private String vendor;

    private String name;

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

    @Override
    public int accuracy() {
        return accuracy;
    }

    @Override
    public void accuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String vendor() {
        return vendor;
    }

    @Override
    public void vendor(@NonNull String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(@NonNull String name) {
        this.name = name;
    }
}

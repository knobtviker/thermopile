package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.SingleValue;

import io.reactivex.annotations.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by bojan on 27/12/2017.
 */

public class Pressure extends RealmObject implements SingleValue {

    public Pressure(final long timestamp, final float value, final int accuracy, @NonNull final String vendor, @NonNull final String name) {
        timestamp(timestamp);
        value(value);
        accuracy(accuracy);
        vendor(vendor);
        name(name);
    }

    public Pressure(){}

    @Index
    private long timestamp;

    private float value;

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
    public float value() {
        return value;
    }

    @Override
    public void value(float value) {
        this.value = value;
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

package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.SingleValue;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by bojan on 27/12/2017.
 */

public class Pressure extends RealmObject implements SingleValue {

    public Pressure(final long timestamp, final float value) {
        timestamp(timestamp);
        value(value);
    }

    public Pressure(){}

    @Index
    private long timestamp;

    private float value;

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
}

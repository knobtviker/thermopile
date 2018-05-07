package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;

import io.objectbox.annotation.Entity;

@Entity
public class Altitude extends SingleModel {

    public Altitude(final long timestamp, final float value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}

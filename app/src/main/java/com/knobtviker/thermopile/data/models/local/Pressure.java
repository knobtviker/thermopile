package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;

import io.objectbox.annotation.Entity;

@Entity
public class Pressure extends SingleModel {

    public Pressure(final long timestamp, final float value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}
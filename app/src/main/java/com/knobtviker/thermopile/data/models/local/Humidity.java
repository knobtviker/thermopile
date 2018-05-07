package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;

import io.objectbox.annotation.Entity;

@Entity
public class Humidity extends SingleModel {

    public Humidity(final long timestamp, final float value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}

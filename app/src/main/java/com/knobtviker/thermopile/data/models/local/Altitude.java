package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.SingleModel;

import io.objectbox.annotation.Entity;

@Entity
public class Altitude extends SingleModel {

    public Altitude(final long timestamp, @NonNull final String vendor, @NonNull final String name, final float value) {
        this.timestamp = timestamp;
        this.vendor = vendor;
        this.name = name;
        this.value = value;
    }
}

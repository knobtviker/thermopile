package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.CartesianModel;

import io.objectbox.annotation.Entity;

@Entity
public class Acceleration extends CartesianModel {

    public Acceleration(final long timestamp, @NonNull final String vendor, @NonNull final String name, final float valueX, final float valueY, final float valueZ) {
        this.timestamp = timestamp;
        this.vendor = vendor;
        this.name = name;
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
    }
}

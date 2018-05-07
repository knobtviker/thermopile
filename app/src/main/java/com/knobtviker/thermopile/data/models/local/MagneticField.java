package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.CartesianModel;

import io.objectbox.annotation.Entity;

@Entity
public class MagneticField extends CartesianModel {

    public MagneticField(final long timestamp, final float valueX, final float valueY, final float valueZ) {
        this.timestamp = timestamp;
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
    }
}

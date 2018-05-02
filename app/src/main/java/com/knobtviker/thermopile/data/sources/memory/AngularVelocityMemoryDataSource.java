package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractCartesianValueMemoryDataSource;

import javax.inject.Inject;

public class AngularVelocityMemoryDataSource extends AbstractCartesianValueMemoryDataSource {

    @Inject
    public AngularVelocityMemoryDataSource() {
        super(ADDRESS_ANGULAR_VELOCITY_X, ADDRESS_ANGULAR_VELOCITY_Y, ADDRESS_ANGULAR_VELOCITY_Z);
    }
}

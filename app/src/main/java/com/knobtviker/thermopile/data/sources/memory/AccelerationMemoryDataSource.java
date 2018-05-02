package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractCartesianValueMemoryDataSource;

import javax.inject.Inject;

public class AccelerationMemoryDataSource extends AbstractCartesianValueMemoryDataSource {

    @Inject
    public AccelerationMemoryDataSource() {
        super(ADDRESS_ACCELERATION_X, ADDRESS_ACCELERATION_Y, ADDRESS_ACCELERATION_Z);
    }
}

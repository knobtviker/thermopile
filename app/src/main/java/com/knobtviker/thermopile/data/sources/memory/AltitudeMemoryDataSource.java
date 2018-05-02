package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractSingleValueMemoryDataSource;

import javax.inject.Inject;

public class AltitudeMemoryDataSource extends AbstractSingleValueMemoryDataSource {

    @Inject
    public AltitudeMemoryDataSource() {
        super(ADDRESS_ALTITUDE);
    }
}

package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractSingleValueMemoryDataSource;

import javax.inject.Inject;

public class AirQualityMemoryDataSource extends AbstractSingleValueMemoryDataSource {

    @Inject
    public AirQualityMemoryDataSource() {
        super(ADDRESS_AIR_QUALITY);
    }
}

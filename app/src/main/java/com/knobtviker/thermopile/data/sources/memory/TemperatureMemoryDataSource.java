package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractSingleValueMemoryDataSource;

import javax.inject.Inject;

public class TemperatureMemoryDataSource extends AbstractSingleValueMemoryDataSource {

    @Inject
    public TemperatureMemoryDataSource() {
        super(ADDRESS_TEMPERATURE);
    }
}

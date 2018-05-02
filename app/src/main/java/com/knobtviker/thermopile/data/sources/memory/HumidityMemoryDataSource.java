package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractSingleValueMemoryDataSource;

import javax.inject.Inject;

public class HumidityMemoryDataSource extends AbstractSingleValueMemoryDataSource {

    @Inject
    public HumidityMemoryDataSource() {
        super(ADDRESS_HUMIDITY);
    }
}

package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractSingleValueMemoryDataSource;

public class PressureMemoryDataSource extends AbstractSingleValueMemoryDataSource {

    public PressureMemoryDataSource() {
        super(ADDRESS_PRESSURE);
    }
}

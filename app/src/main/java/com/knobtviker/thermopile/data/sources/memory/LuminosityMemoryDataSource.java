package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractSingleValueMemoryDataSource;

public class LuminosityMemoryDataSource extends AbstractSingleValueMemoryDataSource {

    public LuminosityMemoryDataSource() {
        super(ADDRESS_LUMINOSITY);
    }
}

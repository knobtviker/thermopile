package com.knobtviker.thermopile.data.sources.memory;

import com.knobtviker.thermopile.data.sources.memory.implementation.AbstractCartesianValueMemoryDataSource;

public class MagneticFieldMemoryDataSource extends AbstractCartesianValueMemoryDataSource {

    public MagneticFieldMemoryDataSource() {
        super(ADDRESS_MAGNETIC_FIELD_X, ADDRESS_MAGNETIC_FIELD_Y, ADDRESS_MAGNETIC_FIELD_Z);
    }
}

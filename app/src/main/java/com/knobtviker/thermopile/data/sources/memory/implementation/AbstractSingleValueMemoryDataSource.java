package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

public abstract class AbstractSingleValueMemoryDataSource extends AbstractMemoryDataSource implements SingleValueMemoryDataSource {

    public AbstractSingleValueMemoryDataSource(final int address) {
        super();

        this.address = address;
    }

    @Override
    public void save(float value) throws IOException {
        saveInt(address, (int) value * 10);
    }

    @Override
    public float load() throws IOException {
        return readInt(address)/10.0f;
    }
}

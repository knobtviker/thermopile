package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

public abstract class AbstractCartesianValueMemoryDataSource extends AbstractMemoryDataSource implements CartesianValueMemoryDataSource {

    public AbstractCartesianValueMemoryDataSource(final int addressX, final int addressY, final int addressZ) {
        super();

        this.addressX = addressX;
        this.addressY = addressY;
        this.addressZ = addressZ;
    }

    @Override
    public void save(float valueX, float valueY, float valueZ) throws IOException {
        saveInt(addressX, (int) valueX * 10);
        saveInt(addressY, (int) valueY * 10);
        saveInt(addressZ, (int) valueZ * 10);
    }

    @Override
    public float[] load() throws IOException {
        final float[] buffer = new float[3];

        buffer[0] = readInt(addressX)/10.0f;
        buffer[1] = readInt(addressY)/10.0f;
        buffer[2] = readInt(addressZ)/10.0f;

        return buffer;
    }
}

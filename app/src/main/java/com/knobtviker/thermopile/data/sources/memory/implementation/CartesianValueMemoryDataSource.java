package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

public interface CartesianValueMemoryDataSource {

    void save(final float valueX, final float valueY, final float valueZ) throws IOException;

    float[] load() throws IOException;
}

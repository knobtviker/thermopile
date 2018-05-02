package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

public interface SingleValueMemoryDataSource {

    void save(final float value) throws IOException;

    float load() throws IOException;
}

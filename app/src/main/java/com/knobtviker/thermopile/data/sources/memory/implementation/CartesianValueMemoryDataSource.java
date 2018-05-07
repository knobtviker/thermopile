package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface CartesianValueMemoryDataSource {

    Completable save(final float[] values) throws IOException;

    Observable<float[]> load() throws IOException;
}

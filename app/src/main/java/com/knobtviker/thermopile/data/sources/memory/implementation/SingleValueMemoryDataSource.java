package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SingleValueMemoryDataSource {

    Completable save(final float value) throws IOException;

    Observable<Float> load() throws IOException;
}

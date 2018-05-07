package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Observable;

public abstract class AbstractCartesianValueMemoryDataSource extends AbstractMemoryDataSource implements CartesianValueMemoryDataSource {

    public AbstractCartesianValueMemoryDataSource(final int addressX, final int addressY, final int addressZ) {
        this.addressX = addressX;
        this.addressY = addressY;
        this.addressZ = addressZ;
    }

    @Override
    public Completable save(float[] values) {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    try {
                        saveInt(addressX, (int) values[0] * 10);
                        saveInt(addressY, (int) values[1] * 10);
                        saveInt(addressZ, (int) values[2] * 10);

                        emitter.onComplete();
                    } catch (IOException e) {
                        emitter.onError(e);
                    }
                }
            })
        );
    }

    @Override
    public Observable<float[]> load() {
        return Observable.defer(() ->
            Observable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    try {
                        final float[] buffer = new float[3];

                        buffer[0] = readInt(addressX) / 10.0f;
                        buffer[1] = readInt(addressY) / 10.0f;
                        buffer[2] = readInt(addressZ) / 10.0f;

                        emitter.onNext(buffer);
                    } catch (IOException e) {
                        emitter.onError(e);
                    }
                    emitter.onComplete();
                }
            })
        );
    }
}

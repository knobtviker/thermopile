package com.knobtviker.thermopile.data.sources.memory.implementation;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public abstract class AbstractSingleValueMemoryDataSource extends AbstractMemoryDataSource implements SingleValueMemoryDataSource {

    public AbstractSingleValueMemoryDataSource(final int address) {
        this.address = address;
    }

    @Override
    public Completable save(float value) {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            saveInt(address, (int) value * 10);
                            emitter.onComplete();
                        } catch (IOException e) {
                            emitter.onError(e);
                        }
                    }
                }
            )
        );
    }

    @Override
    public Observable<Float> load() {
        return Observable.defer(() ->
            Observable.create((ObservableEmitter<Float> emitter) -> {
                    if (!emitter.isDisposed()) {
                        try {
                            final float result = readInt(address) / 10.0f;
                            emitter.onNext(result);
                        } catch (IOException e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();
                    }
                }
            )
        );
    }
}

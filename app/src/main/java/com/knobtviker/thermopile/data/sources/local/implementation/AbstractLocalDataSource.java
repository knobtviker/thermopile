package com.knobtviker.thermopile.data.sources.local.implementation;

import android.support.annotation.NonNull;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.exception.DbException;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataSubscription;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Observable;

public abstract class AbstractLocalDataSource<T> implements BaseLocalDataSource<T> {

    protected Box<T> box;

    public AbstractLocalDataSource(@NonNull final Class<T> clazz) {
        box = Database.getInstance().boxFor(clazz);
    }

    @Override
    public Flowable<T> observe(@NonNull final Query<T> query) {
        return Flowable.create((FlowableEmitter<T> emitter) -> {
                final DataSubscription dataSubscription = query
                    .subscribe()
                    .onError(emitter::onError)
                    .observer(data -> {
                        if (!emitter.isCancelled() && !data.isEmpty()) {
                            emitter.onNext(data.get(data.size()-1));
                        }
                    });
                emitter.setCancellable(dataSubscription::cancel);
            },
            BackpressureStrategy.LATEST);
    }

    @Override
    public Observable<List<T>> query(@NonNull final Query<T> query) {
        return Observable.create(emitter -> {
            final DataSubscription dataSubscription = query
                .subscribe()
                .onError(emitter::onError)
                .observer(data -> {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(data);
                        emitter.onComplete();
                    }
                });
            emitter.setCancellable(dataSubscription::cancel);
        });
    }

    @Override
    public Observable<T> queryById(@NonNull final Query<T> query) {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    final T item = query.findUnique();

                    if (item != null) {
                        emitter.onNext(item);
                    } else {
                        emitter.onError(new Throwable("Item with Id not found"));
                    }
                } catch (DbException exception) {
                    emitter.onError(exception);
                }
                emitter.onComplete();
            }
        });
    }

    @Override
    public Completable save(@NonNull final List<T> items) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                box.put(items);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<Long> save(T item) {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                final long id = box.put(item);
                if (id > 0) {
                    emitter.onNext(id);
                } else {
                    emitter.onError(new Throwable("Item Id cannot be 0"));
                }
                emitter.onComplete();
            }
        });
    }

    @Override
    public Completable removeById(long id) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                box.remove(id);
                emitter.onComplete();
            }
        });
    }

    public abstract Flowable<T> observe();

    public abstract Observable<List<T>> query();

    public abstract Observable<T> queryById(final long id);

    public abstract Observable<List<T>> queryBetween(final long start, final long end);
}

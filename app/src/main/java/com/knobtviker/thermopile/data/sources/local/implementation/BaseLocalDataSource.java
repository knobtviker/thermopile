package com.knobtviker.thermopile.data.sources.local.implementation;

import java.util.List;

import io.objectbox.query.Query;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public interface BaseLocalDataSource<T> {

    Flowable<List<T>> observe(@NonNull final Query<T> query);

    Observable<List<T>> query(@NonNull final Query<T> query);

    Observable<T> queryById(@NonNull final Query<T> query);

    Completable save(@NonNull final List<T> items);

    Observable<Long> save(@NonNull final T item);

    Completable removeById(final long id);
}

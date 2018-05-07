package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;


/**
 * Created by bojan on 17/07/2017.
 */

public class ThresholdRepository extends AbstractRepository {

    @Inject
    ThresholdLocalDataSource thresholdLocalDataSource;

    @Inject
    ThresholdRepository() {
    }

    public Observable<List<Threshold>> load() {
        return thresholdLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<Threshold>> loadByDay(final int day) {
        return thresholdLocalDataSource
            .queryByDay(day)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Threshold> loadById(final long thresholdId) {
        return thresholdLocalDataSource
            .queryById(thresholdId)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Long> save(@NonNull final Threshold item) {
        return thresholdLocalDataSource
            .save(item)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Completable removeById(final long id) {
        return thresholdLocalDataSource
            .removeById(id)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }
}

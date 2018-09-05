package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.Temperature_;
import com.knobtviker.thermopile.data.sources.local.shared.base.AbstractLocalDataSource;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.BoxStore;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class TemperatureLocalDataSource extends AbstractLocalDataSource<Temperature> {

    @Inject
    public TemperatureLocalDataSource(@NonNull final BoxStore boxStore) {
        super(boxStore.boxFor(Temperature.class));
    }

    @Override
    public Flowable<Temperature> observe() {
        return super.observe(
            box.query()
                .orderDesc(Temperature_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Temperature>> query() {
        return super.query(
            box.query()
                .order(Temperature_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Temperature> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Temperature_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Temperature>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(Temperature_.timestamp)
                .between(Temperature_.timestamp, start, end)
                .between(Temperature_.value, MeasuredTemperature.MINIMUM, MeasuredTemperature.MAXIMUM)
                .build()
        );
    }
}

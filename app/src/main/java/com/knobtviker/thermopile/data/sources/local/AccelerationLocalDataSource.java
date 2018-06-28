package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.Acceleration_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class AccelerationLocalDataSource extends AbstractLocalDataSource<Acceleration> {

    @Inject
    public AccelerationLocalDataSource() {
        super(Acceleration.class);
    }

    @Override
    public Flowable<List<Acceleration>> observe() {
        return super.observe(
            box.query()
                .order(Acceleration_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Acceleration>> query() {
        return super.query(
            box.query()
                .order(Acceleration_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Acceleration> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Acceleration_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Acceleration>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(Acceleration_.timestamp)
                .between(Acceleration_.timestamp, start, end)
                .build()
        );
    }
}

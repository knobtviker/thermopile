package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.local.Threshold_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class ThresholdLocalDataSource extends AbstractLocalDataSource<Threshold> {

    @Inject
    public ThresholdLocalDataSource() {
        super(Threshold.class);
    }

    @Override
    public Flowable<List<Threshold>> observe() {
        return super.observe(
            box.query()
                .order(Threshold_.day)
                .order(Threshold_.startHour)
                .order(Threshold_.startMinute)
                .build()
        );
    }

    @Override
    public Observable<List<Threshold>> query() {
        return super.query(
            box.query()
                .order(Threshold_.day)
                .order(Threshold_.startHour)
                .order(Threshold_.startMinute)
                .build()
        );
    }

    @Override
    public Observable<Threshold> queryById(long id) {
        return super.queryById(
            box.query()
                .equal(Threshold_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Threshold>> queryBetween(long start, long end) {
        return Observable.empty();
    }
}

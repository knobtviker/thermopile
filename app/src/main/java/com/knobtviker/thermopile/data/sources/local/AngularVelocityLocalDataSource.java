package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.AngularVelocity_;
import com.knobtviker.thermopile.data.sources.local.shared.base.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class AngularVelocityLocalDataSource extends AbstractLocalDataSource<AngularVelocity> {

    @Inject
    public AngularVelocityLocalDataSource() {
        super(AngularVelocity.class);
    }

    @Override
    public Flowable<AngularVelocity> observe() {
        return super.observe(
            box.query()
                .orderDesc(AngularVelocity_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<AngularVelocity>> query() {
        return super.query(
            box.query()
                .order(AngularVelocity_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<AngularVelocity> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(AngularVelocity_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<AngularVelocity>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(AngularVelocity_.timestamp)
                .between(AngularVelocity_.timestamp, start, end)
                .build()
        );
    }
}

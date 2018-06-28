package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.MagneticField_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class MagneticFieldLocalDataSource extends AbstractLocalDataSource<MagneticField> {

    @Inject
    public MagneticFieldLocalDataSource() {
        super(MagneticField.class);
    }

    @Override
    public Flowable<List<MagneticField>> observe() {
        return super.observe(
            box.query()
                .order(MagneticField_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<MagneticField>> query() {
        return super.query(
            box.query()
                .order(MagneticField_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<MagneticField> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(MagneticField_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<MagneticField>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(MagneticField_.timestamp)
                .between(MagneticField_.timestamp, start, end)
                .build()
        );
    }
}

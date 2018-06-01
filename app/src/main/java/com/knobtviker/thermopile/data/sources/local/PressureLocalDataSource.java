package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Pressure_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class PressureLocalDataSource extends AbstractLocalDataSource<Pressure> {

    @Inject
    public PressureLocalDataSource() {
        super(Pressure.class);
    }

    @Override
    public Observable<List<Pressure>> observe() {
        return super.observe(
            box.query()
                .order(Pressure_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Pressure>> query() {
        return super.query(
            box.query()
                .order(Pressure_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Pressure> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Pressure_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Pressure>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(Pressure_.timestamp)
                .between(Pressure_.timestamp, start, end)
                .between(Pressure_.value, Constants.MEASURED_PRESSURE_MIN, Constants.MEASURED_PRESSURE_MAX)
                .build()
        );
    }
}

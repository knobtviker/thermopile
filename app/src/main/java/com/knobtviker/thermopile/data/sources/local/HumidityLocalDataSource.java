package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Humidity_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class HumidityLocalDataSource extends AbstractLocalDataSource<Humidity> {

    @Inject
    public HumidityLocalDataSource() {
        super(Humidity.class);
    }

    @Override
    public Observable<List<Humidity>> observe() {
        return super.observe(
            box.query()
                .order(Humidity_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Humidity>> query() {
        return super.query(
            box.query()
                .order(Humidity_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Humidity> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Humidity_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Humidity>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(Humidity_.timestamp)
                .between(Humidity_.timestamp, start, end)
                .between(Humidity_.value, Constants.MEASURED_HUMIDITY_MIN, Constants.MEASURED_HUMIDITY_MAX)
                .build()
        );
    }
}

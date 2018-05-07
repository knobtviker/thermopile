package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.AirQuality_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class AirQualityLocalDataSource extends AbstractLocalDataSource<AirQuality> {

    @Inject
    public AirQualityLocalDataSource() {
        super(AirQuality.class);
    }

    @Override
    public Observable<List<AirQuality>> observe() {
        return super.observe(
            box.query()
                .order(AirQuality_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<AirQuality>> query() {
        return super.query(
            box.query()
                .order(AirQuality_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<AirQuality> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(AirQuality_.id, id)
                .build()
        );
    }
}

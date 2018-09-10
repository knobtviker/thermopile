package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.AirQuality_;
import com.knobtviker.thermopile.data.sources.local.shared.base.AbstractLocalDataSource;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAirQuality;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.BoxStore;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class AirQualityLocalDataSource extends AbstractLocalDataSource<AirQuality> {

    @Inject
    public AirQualityLocalDataSource(@NonNull final BoxStore boxStore) {
        super(boxStore.boxFor(AirQuality.class));
    }

    @Override
    public Flowable<AirQuality> observe() {
        return super.observe(
            box.query()
                .orderDesc(AirQuality_.timestamp)
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

    @Override
    public Observable<List<AirQuality>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(AirQuality_.timestamp)
                .between(AirQuality_.timestamp, start, end)
                .between(AirQuality_.value, MeasuredAirQuality.MINIMUM, MeasuredAirQuality.MAXIMUM)
                .build()
        );
    }
}

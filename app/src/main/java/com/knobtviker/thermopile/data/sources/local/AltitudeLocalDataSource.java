package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.Altitude_;
import com.knobtviker.thermopile.data.sources.local.shared.base.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.BoxStore;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class AltitudeLocalDataSource extends AbstractLocalDataSource<Altitude> {

    @Inject
    public AltitudeLocalDataSource(@NonNull final BoxStore boxStore) {
        super(boxStore.boxFor(Altitude.class));
    }

    @Override
    public Flowable<Altitude> observe() {
        return super.observe(
            box.query()
                .orderDesc(Altitude_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Altitude>> query() {
        return super.query(
            box.query()
                .order(Altitude_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Altitude> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Altitude_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Altitude>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(Altitude_.timestamp)
                .between(Altitude_.timestamp, start, end)
                .build()
        );
    }
}

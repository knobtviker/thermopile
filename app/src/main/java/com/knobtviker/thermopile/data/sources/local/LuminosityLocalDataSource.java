package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Luminosity;
import com.knobtviker.thermopile.data.models.local.Luminosity_;
import com.knobtviker.thermopile.data.sources.local.shared.base.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.BoxStore;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class LuminosityLocalDataSource extends AbstractLocalDataSource<Luminosity> {

    @Inject
    public LuminosityLocalDataSource(@NonNull final BoxStore boxStore) {
        super(boxStore.boxFor(Luminosity.class));
    }

    @Override
    public Flowable<Luminosity> observe() {
        return super.observe(
            box.query()
                .orderDesc(Luminosity_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Luminosity>> query() {
        return super.query(
            box.query()
                .order(Luminosity_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Luminosity> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Luminosity_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Luminosity>> queryBetween(long start, long end) {
        return super.query(
            box.query()
                .order(Luminosity_.timestamp)
                .between(Luminosity_.timestamp, start, end)
                .build()
        );
    }
}

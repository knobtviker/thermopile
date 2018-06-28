package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Settings_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class SettingsLocalDataSource extends AbstractLocalDataSource<Settings> {

    @Inject
    public SettingsLocalDataSource() {
        super(Settings.class);
    }

    @Override
    public Flowable<Settings> observe() {
        return super.observe(
            box.query()
                .order(Settings_.id)
                .build()
        );
    }

    @Override
    public Observable<List<Settings>> query() {
        return super.query(
            box.query()
                .order(Settings_.id)
                .build()
        );
    }

    @Override
    public Observable<Settings> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Settings_.id, id)
                .build()
        );
    }

    @Override
    public Observable<List<Settings>> queryBetween(long start, long end) {
        return Observable.empty();
    }
}

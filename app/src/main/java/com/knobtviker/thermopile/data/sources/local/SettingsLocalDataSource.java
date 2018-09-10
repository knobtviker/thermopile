package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Settings_;
import com.knobtviker.thermopile.data.sources.local.shared.base.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.BoxStore;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class SettingsLocalDataSource extends AbstractLocalDataSource<Settings> {

    @Inject
    public SettingsLocalDataSource(@NonNull final BoxStore boxStore) {
        super(boxStore.boxFor(Settings.class));
    }

    @Override
    public Flowable<Settings> observe() {
        return super.observe(
            box.query()
                .orderDesc(Settings_.id)
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

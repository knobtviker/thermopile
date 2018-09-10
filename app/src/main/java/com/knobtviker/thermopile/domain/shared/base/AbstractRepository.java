package com.knobtviker.thermopile.domain.shared.base;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.schedulers.Schedulers;

/**
 * Created by bojan on 13/12/2017.
 */

public abstract class AbstractRepository {

    @NonNull
    protected Schedulers schedulers;

    protected AbstractRepository(@NonNull final Schedulers schedulers) {
        this.schedulers = schedulers;
    }
}

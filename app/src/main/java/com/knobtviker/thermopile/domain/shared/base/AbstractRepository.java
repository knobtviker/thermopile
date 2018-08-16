package com.knobtviker.thermopile.domain.shared.base;

import com.knobtviker.thermopile.di.components.domain.schedulers.DaggerSchedulersComponent;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;

/**
 * Created by bojan on 13/12/2017.
 */

public abstract class AbstractRepository {

    protected final Schedulers schedulers;

    protected AbstractRepository() {
        this.schedulers = DaggerSchedulersComponent.create().inject();
    }
}

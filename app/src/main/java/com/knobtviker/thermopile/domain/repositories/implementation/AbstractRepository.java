package com.knobtviker.thermopile.domain.repositories.implementation;

import com.knobtviker.thermopile.di.components.domain.DaggerSchedulerProviderComponent;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;

/**
 * Created by bojan on 13/12/2017.
 */

public abstract class AbstractRepository {

    protected final SchedulerProvider schedulerProvider;

    protected AbstractRepository() {
        this.schedulerProvider = DaggerSchedulerProviderComponent.create().scheduler();
    }
}

package com.knobtviker.thermopile.domain.repositories.implementation;


import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;

/**
 * Created by bojan on 14/06/2017.
 */

public abstract class BaseRepository {

    protected final SchedulerProvider schedulerProvider;

    protected BaseRepository() {
        this.schedulerProvider = SchedulerProvider.getInstance();
    }

}

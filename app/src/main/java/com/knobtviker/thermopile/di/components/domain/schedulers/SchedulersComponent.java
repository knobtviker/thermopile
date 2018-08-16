package com.knobtviker.thermopile.di.components.domain.schedulers;

import com.knobtviker.thermopile.di.components.domain.repositories.shared.base.BaseComponent;
import com.knobtviker.thermopile.di.modules.domain.schedulers.SchedulersModule;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = SchedulersModule.class)
@Singleton
public interface SchedulersComponent extends BaseComponent<Schedulers> {

    @Override
    Schedulers inject();
}

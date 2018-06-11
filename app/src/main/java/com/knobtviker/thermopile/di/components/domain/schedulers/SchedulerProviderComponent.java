package com.knobtviker.thermopile.di.components.domain.schedulers;

import com.knobtviker.thermopile.di.modules.domain.schedulers.SchedulerProviderModule;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = SchedulerProviderModule.class)
@Singleton
public interface SchedulerProviderComponent {

    SchedulerProvider scheduler();
}

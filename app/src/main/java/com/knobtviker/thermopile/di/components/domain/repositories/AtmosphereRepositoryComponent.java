package com.knobtviker.thermopile.di.components.domain.repositories;

import com.knobtviker.thermopile.di.modules.domain.repositories.AtmosphereRepositoryModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = AtmosphereRepositoryModule.class)
public interface AtmosphereRepositoryComponent {

    AtmosphereRepository repository();
}

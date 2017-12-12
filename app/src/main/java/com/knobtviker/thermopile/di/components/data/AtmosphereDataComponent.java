package com.knobtviker.thermopile.di.components.data;

import com.knobtviker.thermopile.di.modules.data.AtmosphereDataModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = AtmosphereDataModule.class)
public interface AtmosphereDataComponent {

    AtmosphereRepository repository();
}

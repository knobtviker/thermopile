package com.knobtviker.thermopile.di.components.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.memory.AtmosphereMemoryDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = {AtmosphereMemoryDataSourceModule.class, AtmosphereLocalDataSourceModule.class})
public interface AtmosphereRepositoryComponent {

    AtmosphereRepository repository();

    @Component.Builder
    interface Builder {

        Builder localDataSource(@NonNull final AtmosphereLocalDataSourceModule localDataSourceModule);

        Builder memoryDataSource(@NonNull final AtmosphereMemoryDataSourceModule memoryDataSourceModule);

        AtmosphereRepositoryComponent build();
    }
}

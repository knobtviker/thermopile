package com.knobtviker.thermopile.di.components.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.shared.base.BaseComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = {AtmosphereLocalDataSourceModule.class})
@Singleton
public interface AtmosphereRepositoryComponent extends BaseComponent<AtmosphereRepository> {

    @Override
    AtmosphereRepository inject();

    @Component.Builder
    interface Builder {

        Builder localDataSource(@NonNull final AtmosphereLocalDataSourceModule localDataSourceModule);

        AtmosphereRepositoryComponent build();
    }
}

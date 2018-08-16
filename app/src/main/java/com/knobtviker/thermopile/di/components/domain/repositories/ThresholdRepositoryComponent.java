package com.knobtviker.thermopile.di.components.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.shared.base.BaseComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.ThresholdLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = ThresholdLocalDataSourceModule.class)
@Singleton
public interface ThresholdRepositoryComponent extends BaseComponent<ThresholdRepository> {

    @Override
    ThresholdRepository inject();

    @Component.Builder
    interface Builder {

        Builder localDataSource(@NonNull final ThresholdLocalDataSourceModule localDataSourceModule);

        ThresholdRepositoryComponent build();
    }
}

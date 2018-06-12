package com.knobtviker.thermopile.di.components.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.modules.data.sources.local.ThresholdLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = ThresholdLocalDataSourceModule.class)
public interface ThresholdRepositoryComponent {

    ThresholdRepository repository();

    @Component.Builder
    interface Builder {

        Builder localDataSource(@NonNull final ThresholdLocalDataSourceModule localDataSourceModule);

        ThresholdRepositoryComponent build();
    }
}

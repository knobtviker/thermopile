package com.knobtviker.thermopile.di.components.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = SettingsLocalDataSourceModule.class)
public interface SettingsRepositoryComponent {

    SettingsRepository repository();

    @Component.Builder
    interface Builder {

        Builder localDataSource(@NonNull final SettingsLocalDataSourceModule localDataSourceModule);

        SettingsRepositoryComponent build();
    }
}

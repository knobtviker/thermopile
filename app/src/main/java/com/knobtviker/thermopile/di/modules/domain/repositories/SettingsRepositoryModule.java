package com.knobtviker.thermopile.di.modules.domain.repositories;

import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class SettingsRepositoryModule {

    @Provides
    static SettingsLocalDataSource provideLocalDataSource() {
        return new SettingsLocalDataSource();
    }
}

package com.knobtviker.thermopile.di.modules.data;

import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class AtmosphereDataModule {

    @Provides
    static AtmosphereLocalDataSource provideLocalDataSource() {
        return new AtmosphereLocalDataSource();
    }
}

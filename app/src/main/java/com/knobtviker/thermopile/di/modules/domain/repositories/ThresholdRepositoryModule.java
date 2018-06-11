package com.knobtviker.thermopile.di.modules.domain.repositories;

import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class ThresholdRepositoryModule {

    @Provides
    static ThresholdLocalDataSource provideLocalDataSource() {
        return new ThresholdLocalDataSource();
    }
}

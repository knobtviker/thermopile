package com.knobtviker.thermopile.di.modules.data.sources.local;

import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class ThresholdLocalDataSourceModule {

    @Provides
    static ThresholdLocalDataSource provideLocalDataSource() {
        return new ThresholdLocalDataSource();
    }
}

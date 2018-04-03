package com.knobtviker.thermopile.di.modules.data;

import com.knobtviker.thermopile.data.sources.local.PeripheralLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.PeripheralRawDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class PeripheralsDataModule {

    @Provides
    static PeripheralLocalDataSource provideLocalDataSource() {
        return new PeripheralLocalDataSource();
    }

    @Provides
    static PeripheralRawDataSource provideRawDataSource() {
        return new PeripheralRawDataSource();
    }
}

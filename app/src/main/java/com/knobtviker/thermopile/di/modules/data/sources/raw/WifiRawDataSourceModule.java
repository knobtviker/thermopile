package com.knobtviker.thermopile.di.modules.data.sources.raw;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.sources.raw.network.WifiRawDataSource;
import com.knobtviker.thermopile.di.qualifiers.presentation.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class WifiRawDataSourceModule {

    @Provides
    static WifiRawDataSource provideRawDataSource(@NonNull @ActivityScope final Context context) {
        return new WifiRawDataSource(context);
    }
}
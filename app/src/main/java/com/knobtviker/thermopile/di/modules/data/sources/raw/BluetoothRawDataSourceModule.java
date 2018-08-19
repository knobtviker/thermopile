package com.knobtviker.thermopile.di.modules.data.sources.raw;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.sources.raw.bluetooth.BluetoothRawDataSource;
import com.knobtviker.thermopile.di.qualifiers.presentation.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class BluetoothRawDataSourceModule {

    @Provides
    static BluetoothRawDataSource provideRawDataSource(@NonNull @ActivityScope final Context context) {
        return new BluetoothRawDataSource(context);
    }
}

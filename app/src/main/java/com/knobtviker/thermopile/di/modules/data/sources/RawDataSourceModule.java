package com.knobtviker.thermopile.di.modules.data.sources;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.sources.raw.bluetooth.BluetoothRawDataSource;
import com.knobtviker.thermopile.data.sources.raw.network.WifiRawDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class RawDataSourceModule {

    @Provides
    @Singleton
    static BluetoothRawDataSource provideBluetoothRawDataSource(@NonNull final Context context) {
        return new BluetoothRawDataSource(context);
    }

    @Provides
    @Singleton
    static WifiRawDataSource provideWifiRawDataSource(@NonNull final Context context) {
        return new WifiRawDataSource(context);
    }
}

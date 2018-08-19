package com.knobtviker.thermopile.di.components.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.shared.base.BaseComponent;
import com.knobtviker.thermopile.di.modules.data.sources.raw.BluetoothRawDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.raw.WifiRawDataSourceModule;
import com.knobtviker.thermopile.di.modules.presentation.ContextModule;
import com.knobtviker.thermopile.domain.repositories.NetworkRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = {ContextModule.class, WifiRawDataSourceModule.class, BluetoothRawDataSourceModule.class})
@Singleton
public interface NetworkRepositoryComponent extends BaseComponent<NetworkRepository> {

    @Override
    NetworkRepository inject();

    @Component.Builder
    interface Builder {

        Builder context(@NonNull final ContextModule contextModule);

        Builder wifiRawDataSource(@NonNull final WifiRawDataSourceModule wifiRawDataSourceModule);

        Builder bluetoothRawDataSource(@NonNull final BluetoothRawDataSourceModule rawDataSourceModule);

        NetworkRepositoryComponent build();
    }
}

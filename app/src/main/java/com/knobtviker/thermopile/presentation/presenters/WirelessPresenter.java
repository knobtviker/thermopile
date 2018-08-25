package com.knobtviker.thermopile.presentation.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerNetworkRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.raw.BluetoothRawDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.raw.WifiRawDataSourceModule;
import com.knobtviker.thermopile.di.modules.presentation.ContextModule;
import com.knobtviker.thermopile.domain.repositories.NetworkRepository;
import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class WirelessPresenter extends AbstractPresenter implements WirelessContract.Presenter {

    private final WirelessContract.View view;

    private final NetworkRepository networkRepository;

    public WirelessPresenter(@NonNull final Context context, @NonNull final WirelessContract.View view) {
        super(view);

        this.view = view;
        this.networkRepository = DaggerNetworkRepositoryComponent.builder()
            .context(new ContextModule(context))
            .wifiRawDataSource(new WifiRawDataSourceModule())
            .bluetoothRawDataSource(new BluetoothRawDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void hasWifi() {
        compositeDisposable.add(
            networkRepository.hasWifi()
                .subscribe(
                    hasWifi -> {
                        if (hasWifi) {
                            setupWifiDevice();
                        }
                        view.onHasWifi(hasWifi);
                    },
                    this::error
                )
        );
    }

    @Override
    public void isWifiEnabled() {
        compositeDisposable.add(
            networkRepository
                .isWifiEnabled()
                .subscribe(
                    view::onIsWifiEnabled,
                    this::error
                )
        );
    }

    @Override
    public void enableWifi() {
        compositeDisposable.add(
            networkRepository
                .enableWifi()
                .subscribe(
                    () -> view.onIsWifiEnabled(true),
                    this::error
                )
        );
    }

    @Override
    public void scan() {
        compositeDisposable.add(
            networkRepository
                .scanWifi()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void observeScanResults() {
        compositeDisposable.add(
            networkRepository
                .observeScanResults()
                .subscribe(
                    view::onScanResults,
                    this::error
                )
        );
    }

    @Override
    public void connectWifi(@NonNull String ssid, @Nullable String password, @WiFiType String type) {
        compositeDisposable.add(
            networkRepository
                .connectWifi(ssid, password, type)
                .subscribe(
                    view::onConnectWifi,
                    this::error
                )
        );
    }

    private void setupWifiDevice() {
        compositeDisposable.add(
            networkRepository
                .setupWifiDevice()
                .subscribe(
                    this::isWifiEnabled,
                    this::error
                )
        );
    }
}

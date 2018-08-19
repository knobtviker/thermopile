package com.knobtviker.thermopile.presentation.presenters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.sources.raw.bluetooth.GattServerCallback;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerAtmosphereRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerNetworkRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.raw.BluetoothRawDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.raw.WifiRawDataSourceModule;
import com.knobtviker.thermopile.di.modules.presentation.ContextModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.NetworkRepository;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import io.reactivex.Observable;
import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class NetworkPresenter extends AbstractPresenter implements NetworkContract.Presenter {

    private final NetworkContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    private final NetworkRepository networkRepository;

    public NetworkPresenter(@NonNull final Context context, @NonNull final NetworkContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereRepositoryComponent.builder()
            .localDataSource(new AtmosphereLocalDataSourceModule())
            .build()
            .inject();
        this.networkRepository = DaggerNetworkRepositoryComponent.builder()
            .context(new ContextModule(context))
            .wifiRawDataSource(new WifiRawDataSourceModule())
            .bluetoothRawDataSource(new BluetoothRawDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void observeTemperature() {
        compositeDisposable.add(
            atmosphereRepository
                .observeTemperature()
                .subscribe(
                    view::onTemperatureChanged,
                    this::error
                )
        );
    }

    @Override
    public void observePressure() {
        compositeDisposable.add(
            atmosphereRepository
                .observePressure()
                .subscribe(
                    view::onPressureChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeHumidity() {
        compositeDisposable.add(
            atmosphereRepository
                .observeHumidity()
                .subscribe(
                    view::onHumidityChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeAirQuality() {
        compositeDisposable.add(
            atmosphereRepository
                .observeAirQuality()
                .subscribe(
                    view::onAirQualityChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeAcceleration() {
        compositeDisposable.add(
            atmosphereRepository
                .observeAcceleration()
                .subscribe(
                    view::onAccelerationChanged,
                    this::error
                )
        );
    }

    @Override
    public void hasBluetooth() {
        compositeDisposable.add(
            Observable.zip(
                networkRepository.hasBluetooth(),
                networkRepository.hasBluetoothLowEnergy(),
                (hasBluetooth, hasBluetoothLowEnergy) -> hasBluetooth && hasBluetoothLowEnergy
            )
                .subscribe(
                    hasBluetooth -> {
                        view.onHasBluetooth(hasBluetooth);
                        if (hasBluetooth) {
                            isBluetoothEnabled();
                            observeBluetoothState();
                            setupBluetoothDevice();
                        }
                    },
                    this::error
                )
        );
    }

    @Override
    public void isBluetoothEnabled() {
        compositeDisposable.add(
            networkRepository
                .isBluetoothEnabled()
                .subscribe(
                    isEnabled -> {
                        view.onIsBluetoothEnabled(isEnabled);
                        if (isEnabled) {
                            startGattServer();
                            startBluetoothAdvertising();
                        }
                    },
                    this::error
                )
        );
    }

    @Override
    public void enableBluetooth() {
        compositeDisposable.add(
            networkRepository
                .enableBluetooth()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void disableBluetooth() {
        compositeDisposable.add(
            networkRepository
                .disableBluetooth()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void observeBluetoothState() {
        compositeDisposable.add(
            networkRepository
                .observeBluetoothState()
                .subscribe(
                    state -> {
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                view.onBluetoothState(false);
                                stopBluetoothAdvertising();
                                stopGattServer();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                view.onBluetoothState(true);
                                startGattServer();
                                startBluetoothAdvertising();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                            case BluetoothAdapter.STATE_TURNING_ON:
                                view.onBluetoothStateIndeterminate();
                                break;
                        }
                    },
                    this::error
                )
        );
    }

    @Override
    public void startGattServer() {
        compositeDisposable.add(
            networkRepository
                .startGattServer(GattServerCallback.create())
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    //    @Override
    //    public void onGattServerStarted(@NonNull BluetoothGattServer gattServer) {
    //        this.gattServer = gattServer;
    //
    //        //TODO: Finish ESS and ESP BLE profile and service
    //        final EnvironmentProfile environmentProfile = new EnvironmentProfile();
    //        final TimeProfile timeProfile = new TimeProfile();
    //
    //        this.gattServer.addService(environmentProfile.createService());
    //        this.gattServer.addService(timeProfile.createService());
    //    }

    //    @Override
    //    public void onGattSendResponse(@NonNull BluetoothDevice device, int requestId, int status, @NonNull UUID uuid) {
    //        if (gattServer != null) {
    //            byte[] response = new byte[0];
    //            if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE)) {
    //                response = EnvironmentProfile.toByteArray(Math.round(atmosphere.temperature()));
    //                Log.i(TAG, atmosphere.temperature() + "");
    //            } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE)) {
    //                response = EnvironmentProfile.toByteArray(atmosphere.pressure());
    //                Log.i(TAG, atmosphere.pressure() + "");
    //            } else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY)) {
    //                response = EnvironmentProfile.toByteArray(atmosphere.humidity());
    //                Log.i(TAG, atmosphere.humidity() + "");
    //        } else {
    //            Timber.e("Invalid Characteristic Read: %s", uuid);
    //            gattServer.sendResponse(device, requestId, status, 0, null);
    //        }
    //            gattServer.sendResponse(device, requestId, status, 0, response);
    //        }
    //    }

    @Override
    public void stopGattServer() {
        compositeDisposable.add(
            networkRepository
                .stopGattServer()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void startBluetoothAdvertising() {
        compositeDisposable.add(
            networkRepository
                .startBluetoothAdvertising()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void stopBluetoothAdvertising() {
        compositeDisposable.add(
            networkRepository
                .stopBluetoothAdvertising()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void enableDiscoverability(@NonNull Activity activity, int requestCode) {
        networkRepository.enableDiscoverability(activity, requestCode);
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
                    isWifiEnabled -> {
                        view.onIsWifiEnabled(isWifiEnabled);
                        wifiInfo();
                    },
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
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void disableWifi() {
        compositeDisposable.add(
            networkRepository
                .disableWifi()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    private void setupBluetoothDevice() {
        compositeDisposable.add(
            networkRepository
                .setupBluetoothDevice(R.string.app_name)
                .subscribe(
                    view::onBluetoothSetupCompleted,
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

    private void wifiInfo() {
        compositeDisposable.add(
            networkRepository
                .wifiInfo()
                .subscribe(
                    view::onWifiInfo,
                    this::error
                )
        );
    }
}

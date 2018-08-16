package com.knobtviker.thermopile.presentation.presenters;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerAtmosphereRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerNetworkRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.raw.BluetoothRawDataSourceModule;
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
                    view::onHasBluetooth,
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
                    view::onIsBluetoothEnabled,
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
                .observeState()
                .subscribe(
                    state -> {
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                view.onBluetoothState(false);
                                break;
                            case BluetoothAdapter.STATE_ON:
                                view.onBluetoothState(true);
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
}

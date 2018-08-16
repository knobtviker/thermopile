package com.knobtviker.thermopile.domain.repositories;

import com.knobtviker.thermopile.data.sources.raw.bluetooth.BluetoothRawDataSource;
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class NetworkRepository extends AbstractRepository {

    @Inject
    BluetoothRawDataSource bluetoothRawDataSource;

    @Inject
    NetworkRepository() {
    }

    public Observable<Boolean> hasBluetooth() {
        return bluetoothRawDataSource
            .hasBluetooth()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Observable<Boolean> hasBluetoothLowEnergy() {
        return bluetoothRawDataSource
            .hasBluetoothLowEnergy()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Observable<Boolean> isBluetoothEnabled() {
        return bluetoothRawDataSource
            .isEnabled()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Completable enableBluetooth() {
        return bluetoothRawDataSource
            .enable()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Completable disableBluetooth() {
        return bluetoothRawDataSource
            .disable()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Observable<Integer> observeState() {
        return bluetoothRawDataSource
            .state()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }
}

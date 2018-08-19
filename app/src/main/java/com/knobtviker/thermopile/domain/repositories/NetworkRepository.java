package com.knobtviker.thermopile.domain.repositories;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServerCallback;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothProfile;
import com.knobtviker.thermopile.data.sources.raw.bluetooth.BluetoothRawDataSource;
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;

import java.util.Arrays;

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

    public Observable<Integer> observeBluetoothState() {
        return bluetoothRawDataSource
            .state()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Completable name(@StringRes final int resId) {
        return bluetoothRawDataSource
            .name(resId)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable deviceClass() {
        return bluetoothRawDataSource
            .setDeviceClass(
                BluetoothClass.Service.INFORMATION,
                BluetoothClass.Device.COMPUTER_SERVER,
                BluetoothConfigManager.IO_CAPABILITY_OUT
            )
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable profiles() {
        return bluetoothRawDataSource
            .setProfiles(Arrays.asList(BluetoothProfile.GATT, BluetoothProfile.GATT_SERVER))
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable startGattServer(@NonNull final BluetoothGattServerCallback bluetoothGattServerCallback) {
        return bluetoothRawDataSource
            .startGattServer(bluetoothGattServerCallback)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable stopGattServer() {
        return bluetoothRawDataSource
            .stopGattServer()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable startBluetoothAdvertising() {
        return bluetoothRawDataSource
            .startAdvertising()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable stopBluetoothAdvertising() {
        return bluetoothRawDataSource
            .stopGattServer()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }
}

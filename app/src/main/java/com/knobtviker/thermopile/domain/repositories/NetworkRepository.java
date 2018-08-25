package com.knobtviker.thermopile.domain.repositories;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServerCallback;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Pair;

import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothProfile;
import com.knobtviker.thermopile.data.sources.raw.bluetooth.BluetoothRawDataSource;
import com.knobtviker.thermopile.data.sources.raw.network.WifiRawDataSource;
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;
import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by bojan on 17/07/2017.
 */

public class NetworkRepository extends AbstractRepository {

    @Inject
    BluetoothRawDataSource bluetoothRawDataSource;

    @Inject
    WifiRawDataSource wifiRawDataSource;

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

    public Completable setupBluetoothDevice(@StringRes final int resId) {
        return Completable.concatArray(
            name(resId),
            deviceClass(),
            profiles()
        )
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
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

    public void enableDiscoverability(Activity activity, int requestCode) {
        bluetoothRawDataSource.enableDiscoverability(activity, requestCode);
    }

    public Observable<Boolean> hasWifi() {
        return wifiRawDataSource
            .hasWifi()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Completable setupWifiDevice() {
        return wifiRawDataSource
            .setup()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Observable<Boolean> isWifiEnabled() {
        return wifiRawDataSource
            .isEnabled()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Observable<Pair<String, String>> wifiInfo() {
        return wifiRawDataSource.wifiInfo()
            .map(wifiInfo -> {
                final String ssid = wifiInfo.getSSID().replace("\"", "");
                final String ip = extractIP(wifiInfo);
                return Pair.create(ssid, ip);
            })
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    private String extractIP(@NonNull final WifiInfo wifiInfo) {
        int ipAddress = wifiInfo.getIpAddress();
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        final byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        try {
            return InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Timber.e(ex);
            return "";
        }
    }

    public Completable enableWifi() {
        return wifiRawDataSource
            .enable()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable disableWifi() {
        return wifiRawDataSource
            .disable()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Completable scanWifi() {
        return Observable
            .interval(0,30, TimeUnit.SECONDS, schedulers.network)
            .flatMapCompletable(tick -> wifiRawDataSource.scan())
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.network);
    }

    public Observable<List<ScanResult>> observeScanResults() {
        return wifiRawDataSource
            .scanResults()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }

    public Observable<Boolean> connectWifi(@NonNull final String ssid, @Nullable final String password, @WiFiType final String type) {
        return wifiRawDataSource
            .connect(ssid, password, type)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui);
    }
}

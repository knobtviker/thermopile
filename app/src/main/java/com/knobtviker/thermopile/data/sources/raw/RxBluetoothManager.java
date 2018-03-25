package com.knobtviker.thermopile.data.sources.raw;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothProfileManager;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

import static android.content.Context.BLUETOOTH_SERVICE;

public class RxBluetoothManager {

    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;
    private final BluetoothManager bluetoothManager;

    @Nullable
    private BluetoothGattServer gattServer;

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;

    @Nullable
    private AdvertiseCallback advertiseCallback;

    public static RxBluetoothManager with(@NonNull final Context context) {
        return new RxBluetoothManager(context);
    }

    public RxBluetoothManager(@NonNull final Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        this.bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
    }

    /**
     * Return true if Bluetooth is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty,
     * otherwise Bluetooth is not supported on this hardware
     */
    @SuppressLint("HardwareIds")
    public boolean hasBluetooth() {
        return !(bluetoothAdapter == null || TextUtils.isEmpty(bluetoothAdapter.getAddress()));
    }

    /**
     * Return true if Bluetooth LE is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty and system has feature,
     * otherwise Bluetooth LE is not supported on this hardware
     */
    public boolean hasBluetoothLowEnergy() {
        return hasBluetooth() && context.getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Turn on the local Bluetooth adapter
     *
     * @return true to indicate adapter startup has begun, or false immediate error
     */
    public boolean enable() {
        return bluetoothAdapter.enable();
    }

    /**
     * Turn off the local Bluetooth adapter
     *
     * @return true to indicate adapter shutdown has begun, or false on immediate error
     */
    public boolean disable() {
        return bluetoothAdapter.disable();
    }

    /**
     * Observes BluetoothState. Possible values are:
     * {@link BluetoothAdapter#STATE_OFF},
     * {@link BluetoothAdapter#STATE_TURNING_ON},
     * {@link BluetoothAdapter#STATE_ON},
     * {@link BluetoothAdapter#STATE_TURNING_OFF},
     *
     * @return RxJava2 Observable with BluetoothState
     */
    public Observable<Integer> state() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        return Observable.defer(() ->
            Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        emitter.onNext(bluetoothAdapter.getState());
                    }
                };

                context.registerReceiver(receiver, filter);

                emitter.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {
                        context.unregisterReceiver(receiver);

                        dispose();
                    }
                });
            }));
    }

    public void setDeviceClass(final int service, final int device) {
        final BluetoothConfigManager configManager = BluetoothConfigManager.getInstance();
        final BluetoothClass deviceClass = BluetoothClassFactory.build(service, device);

        configManager.setBluetoothClass(deviceClass);
    }

    public void setProfile(final int profile) {
        final BluetoothProfileManager profileManager = BluetoothProfileManager.getInstance();
        final List<Integer> enabledProfiles = profileManager.getEnabledProfiles();
        if (!enabledProfiles.contains(profile)) {
            profileManager.enableProfiles(Collections.singletonList(profile));
        }
    }

    /**
     * Initialize the GATT server instance with callback.
     * May return null if failed.
     */
    @Nullable
    public BluetoothGattServer startGattServer(@NonNull final BluetoothGattServerCallback bluetoothGattServerCallback) {
        gattServer = bluetoothManager.openGattServer(context, bluetoothGattServerCallback);
        return gattServer;
    }

    /**
     * Shut down the GATT server.
     */
    public void stopGattServer() {
        if (gattServer != null) {
            gattServer.close();
            gattServer = null;
        }
    }

    /**
     * Begin advertising over Bluetooth that this device is connectable and supports the selected service.
     */
    public void startAdvertising(@NonNull final AdvertiseSettings settings, @NonNull final AdvertiseData data, @NonNull final AdvertiseCallback callback) {
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.startAdvertising(settings, data, callback);
            this.advertiseCallback = callback;
        }
    }

    /**
     * Stop Bluetooth advertisements.
     */
    public void stopAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            advertiseCallback = null;
        }
    }

}

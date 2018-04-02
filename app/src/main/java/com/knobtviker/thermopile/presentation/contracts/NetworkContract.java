package com.knobtviker.thermopile.presentation.contracts;

import android.app.Activity;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface NetworkContract {

    interface View extends BaseView {

        void onHasBluetooth(final boolean hasBluetooth);

        void onBluetoothEnabled(final boolean isEnabled);

        void onBluetoothStateIndeterminate();

        void onBluetoothState(final boolean isOn);

        void onCheckBluetoothAdvertising(final boolean isAdvertising);

        void onCheckGattServer(final boolean isGattServerRunning);

        void onGattServerStarted(@NonNull final BluetoothGattServer gattServer);
    }

    interface Presenter extends BasePresenter {

        void hasBluetooth();

        void bluetooth(final boolean enable);

        void isBluetoothEnabled();

        void name(@NonNull final String name);

        void discoverable(@NonNull final Activity activity, final int requestCode, final int duration);

        void observeBluetoothState();

        void setBluetoothDeviceClass(final int service, final int device);

        void setBluetoothProfile(final int profile);

        void startGattServer(@NonNull final BluetoothGattServerCallback callback);

        void stopGattServer();

        void isGattServerRunning();

        void startBluetoothAdvertising(@NonNull final AdvertiseSettings settings, @NonNull final AdvertiseData data, @NonNull final AdvertiseCallback callback);

        void stopBluetoothAdvertising();

        void isBluetoothAdvertising();
    }
}

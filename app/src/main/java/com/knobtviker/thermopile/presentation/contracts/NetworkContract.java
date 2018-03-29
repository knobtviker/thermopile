package com.knobtviker.thermopile.presentation.contracts;

import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
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
    }

    interface Presenter extends BasePresenter {

        void hasBluetooth();

        void bluetooth(final boolean enable);

        void isBluetoothEnabled();

        void observeBluetoothState();

        void setBluetoothDeviceClass(final int service, final int device);

        void setBluetoothProfile(final int profile);

        BluetoothGattServer startGattServer(@NonNull final BluetoothGattServerCallback callback);

        void stopGattServer();

        boolean isGattServerRunning();

        void startBluetoothAdvertising();

        void stopBluetoothAdvertising();

        boolean isBluetoothAdvertising();
    }
}

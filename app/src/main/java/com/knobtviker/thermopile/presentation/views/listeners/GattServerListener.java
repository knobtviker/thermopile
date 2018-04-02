package com.knobtviker.thermopile.presentation.views.listeners;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.UUID;

public interface GattServerListener {

    void onGattConnectionStateChange(@NonNull final BluetoothDevice device, final int status, final int newState);

    void onGattSendResponse(@NonNull final BluetoothDevice device, final int requestId, final int status, @NonNull final UUID uuid);

    void onGattDescriptorResponse(@NonNull final BluetoothDevice device, final int requestId, final int status, final int offset, final byte[] value);
}

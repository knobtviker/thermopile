package com.knobtviker.thermopile.data.sources.raw;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GattServerCallback extends BluetoothGattServerCallback {
    private static final String TAG = GattServerCallback.class.getSimpleName();

    final Set<BluetoothDevice> mRegisteredDevices = new HashSet<>();

    @Nullable
    private BluetoothGattServer mBluetoothGattServer;

    public GattServerCallback() {
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
            //Remove device from any active subscriptions
            mRegisteredDevices.remove(device);
        }
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGattServer != null) {
            long now = System.currentTimeMillis();
            if (TimeProfile.CURRENT_TIME.equals(characteristic.getUuid())) {
                Log.i(TAG, "Read CurrentTime");
                mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    TimeProfile.getExactTime(now, TimeProfile.ADJUST_NONE));
            } else if (TimeProfile.LOCAL_TIME_INFO.equals(characteristic.getUuid())) {
                Log.i(TAG, "Read LocalTimeInfo");
                mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    TimeProfile.getLocalTimeInfo(now));
            } else {
                // Invalid characteristic
                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
                mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    null);
            }
        }
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
                                        BluetoothGattDescriptor descriptor) {
        if (mBluetoothGattServer != null) {
            if (TimeProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
                Log.d(TAG, "Config descriptor read");
                byte[] returnValue;
                if (mRegisteredDevices.contains(device)) {
                    returnValue = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                } else {
                    returnValue = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                }
                mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    returnValue);
            } else {
                Log.w(TAG, "Unknown descriptor read request");
                mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    null);
            }
        }
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                         BluetoothGattDescriptor descriptor,
                                         boolean preparedWrite, boolean responseNeeded,
                                         int offset, byte[] value) {
        if (mBluetoothGattServer != null) {
            if (TimeProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: " + device);
                    mRegisteredDevices.add(device);
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Unsubscribe device from notifications: " + device);
                    mRegisteredDevices.remove(device);
                }

                if (responseNeeded) {
                    mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        null);
                }
            } else {
                Log.w(TAG, "Unknown descriptor write request");
                if (responseNeeded) {
                    mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
                }
            }
        }
    }

    public void setGattServer(@Nullable final BluetoothGattServer mBluetoothGattServer) {
        this.mBluetoothGattServer = mBluetoothGattServer;
    }
}

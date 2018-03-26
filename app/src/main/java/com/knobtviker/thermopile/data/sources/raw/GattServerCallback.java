package com.knobtviker.thermopile.data.sources.raw;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.knobtviker.thermopile.presentation.ThermopileApp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GattServerCallback extends BluetoothGattServerCallback {
    private static final String TAG = GattServerCallback.class.getSimpleName();

    final Set<BluetoothDevice> registeredDevicesTemperature = new HashSet<>();
    final Set<BluetoothDevice> registeredDevicesPressure = new HashSet<>();
    final Set<BluetoothDevice> registeredDevicesHumidity = new HashSet<>();

    @Nullable
    private BluetoothGattServer bluetoothGattServer;

    private final ThermopileApp application;

    public GattServerCallback(@NonNull final Activity activity) {
        this.application = (ThermopileApp) activity.getApplication();
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            registeredDevicesTemperature.remove(device);
            registeredDevicesPressure.remove(device);
            registeredDevicesHumidity.remove(device);
        }
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        if (bluetoothGattServer != null) {
            final UUID uuid = characteristic.getUuid();
            byte[] response = new byte[0];
            if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE)) {
                response = EnvironmentProfile.toByteArray(Math.round(application.atmosphere().temperature()));
                Log.i(TAG, application.atmosphere().temperature()+"");
            } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE)) {
//                response = EnvironmentProfile.toByteArray(application.atmosphere().pressure());
                Log.i(TAG, application.atmosphere().pressure()+"");
            } else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY)) {
//                response = EnvironmentProfile.toByteArray(application.atmosphere().humidity());
                Log.i(TAG, application.atmosphere().humidity()+"");
            } else {
                Log.e(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }

            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, response);
        }
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        if (bluetoothGattServer != null) {
            final UUID uuid = descriptor.getUuid();
            if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE_CLIENT_CONFIG)) {
                final byte[] returnValue = registeredDevicesTemperature.contains(device) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
            } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE_CLIENT_CONFIG)) {
                final byte[] returnValue = registeredDevicesPressure.contains(device) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
            }  else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY_CLIENT_CONFIG)) {
                final byte[] returnValue = registeredDevicesHumidity.contains(device) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
            } else {
                Log.e(TAG, "Unknown descriptor read request");
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }
        }
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        if (bluetoothGattServer != null) {
            final UUID uuid = descriptor.getUuid();

            if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE_CLIENT_CONFIG)) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    registeredDevicesTemperature.add(device);
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    registeredDevicesTemperature.remove(device);
                }
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                }
            } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE_CLIENT_CONFIG)) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    registeredDevicesPressure.add(device);
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    registeredDevicesPressure.remove(device);
                }
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                }
            }  else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY_CLIENT_CONFIG)) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    registeredDevicesHumidity.add(device);
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    registeredDevicesHumidity.remove(device);
                }
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                }
            } else {
                Log.e(TAG, "Unknown descriptor write request");
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE,0, null);
                }
            }
        }
    }

    public void setGattServer(@Nullable final BluetoothGattServer mBluetoothGattServer) {
        this.bluetoothGattServer = mBluetoothGattServer;
    }
}

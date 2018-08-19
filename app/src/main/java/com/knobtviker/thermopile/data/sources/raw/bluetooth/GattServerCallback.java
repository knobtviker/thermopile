package com.knobtviker.thermopile.data.sources.raw.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GattServerCallback extends BluetoothGattServerCallback {
    private static final String TAG = GattServerCallback.class.getSimpleName();

    private final Set<BluetoothDevice> registeredDevicesTemperature = new HashSet<>();
    private final Set<BluetoothDevice> registeredDevicesPressure = new HashSet<>();
    private final Set<BluetoothDevice> registeredDevicesHumidity = new HashSet<>();

    public static GattServerCallback create() {
        return new GattServerCallback();
    }

    private GattServerCallback() {

    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            registeredDevicesTemperature.remove(device);
            registeredDevicesPressure.remove(device);
            registeredDevicesHumidity.remove(device);
        }
//        listener.onGattConnectionStateChange(device, status, newState);
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        final UUID uuid = characteristic.getUuid();

//        listener.onGattSendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, uuid);
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        final UUID uuid = descriptor.getUuid();
//        if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE_CLIENT_CONFIG)) {
//            final byte[] returnValue = registeredDevicesTemperature.contains(device) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
//            listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
//        } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE_CLIENT_CONFIG)) {
//            final byte[] returnValue = registeredDevicesPressure.contains(device) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
//            listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
//        } else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY_CLIENT_CONFIG)) {
//            final byte[] returnValue = registeredDevicesHumidity.contains(device) ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
//            listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
//        } else {
//            Log.e(TAG, "Unknown descriptor read request");
//            listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
//        }
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        final UUID uuid = descriptor.getUuid();

//        if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE_CLIENT_CONFIG)) {
//            if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
//                registeredDevicesTemperature.add(device);
//            } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
//                registeredDevicesTemperature.remove(device);
//            }
//            if (responseNeeded) {
//                listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
//            }
//        } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE_CLIENT_CONFIG)) {
//            if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
//                registeredDevicesPressure.add(device);
//            } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
//                registeredDevicesPressure.remove(device);
//            }
//            if (responseNeeded) {
//                listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
//            }
//        } else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY_CLIENT_CONFIG)) {
//            if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
//                registeredDevicesHumidity.add(device);
//            } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
//                registeredDevicesHumidity.remove(device);
//            }
//            if (responseNeeded) {
//                listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
//            }
//        } else {
//            Log.e(TAG, "Unknown descriptor write request");
//            if (responseNeeded) {
//                listener.onGattDescriptorResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
//            }
//        }
    }
}

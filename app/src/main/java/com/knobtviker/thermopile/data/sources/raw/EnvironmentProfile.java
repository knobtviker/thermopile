package com.knobtviker.thermopile.data.sources.raw;
/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Implementation of the Bluetooth GATT Time Profile.
 * https://www.bluetooth.com/specifications/adopted-specifications
 */
public class EnvironmentProfile {
    private static final String TAG = EnvironmentProfile.class.getSimpleName();

    //Service UUID
    public static UUID UUID_ENVIRONMENT_SERVICE = UUID.fromString("6863b307-39bf-49c4-90c6-1df4512c2321");

    // Temperature UUID
    public static UUID UUID_TEMPERATURE = UUID.fromString("6863b707-39bf-49c4-90c6-1df4512c2321");

    // Pressure UUID
    public static UUID UUID_PRESSURE = UUID.fromString("6863b708-39bf-49c4-90c6-1df4512c2321");

    // Humidity UUID
    public static UUID UUID_HUMIDITY = UUID.fromString("6863b709-39bf-49c4-90c6-1df4512c2321");

    // Motion UUID
    public static UUID UUID_MOTION = UUID.fromString("6863b710-39bf-49c4-90c6-1df4512c2321");

    // IAQ UUID
    public static UUID UUID_IAQ = UUID.fromString("6863b711-39bf-49c4-90c6-1df4512c2321");

    // Temperature client config UUID
    public static UUID UUID_TEMPERATURE_CLIENT_CONFIG = UUID.fromString("6863b807-39bf-49c4-90c6-1df4512c2321");

    // Pressure client config UUID
    public static UUID UUID_PRESSURE_CLIENT_CONFIG = UUID.fromString("6863b808-39bf-49c4-90c6-1df4512c2321");

    // Humidity client config UUID
    public static UUID UUID_HUMIDITY_CLIENT_CONFIG = UUID.fromString("6863b809-39bf-49c4-90c6-1df4512c2321");

    // Motion client config UUID
    public static UUID UUID_MOTION_CLIENT_CONFIG = UUID.fromString("6863b810-39bf-49c4-90c6-1df4512c2321");

    // IAQ client config UUID
    public static UUID UUID_IAQ_CLIENT_CONFIG = UUID.fromString("6863b811-39bf-49c4-90c6-1df4512c2321");

    /**
     * Return a configured {@link BluetoothGattService} instance for the
     * Current Time Service.
     */
    public static BluetoothGattService createService() {
        // First define a service
        final BluetoothGattService service = new BluetoothGattService(UUID_ENVIRONMENT_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Define characteristics of your choice (uid, property, permission)
        //Read-only characteristics, supports notifications
        final BluetoothGattCharacteristic temperature = new BluetoothGattCharacteristic(
            UUID_TEMPERATURE,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        );
        final BluetoothGattCharacteristic pressure = new BluetoothGattCharacteristic(
            UUID_PRESSURE,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        );
        final BluetoothGattCharacteristic humidity = new BluetoothGattCharacteristic(
            UUID_HUMIDITY,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        );
        final BluetoothGattCharacteristic motion = new BluetoothGattCharacteristic(
            UUID_MOTION,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        );
        final BluetoothGattCharacteristic iaq = new BluetoothGattCharacteristic(
            UUID_IAQ,
            BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        );

        final BluetoothGattDescriptor temperatureConfigDescriptor = new BluetoothGattDescriptor(
            UUID_TEMPERATURE_CLIENT_CONFIG,
            BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE
        );
        final BluetoothGattDescriptor pressureConfigDescriptor = new BluetoothGattDescriptor(
            UUID_PRESSURE_CLIENT_CONFIG,
            BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE
        );
        final BluetoothGattDescriptor humidityConfigDescriptor = new BluetoothGattDescriptor(
            UUID_HUMIDITY_CLIENT_CONFIG,
            BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE
        );
        final BluetoothGattDescriptor motionConfigDescriptor = new BluetoothGattDescriptor(
            UUID_MOTION_CLIENT_CONFIG,
            BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE
        );
        final BluetoothGattDescriptor iaqConfigDescriptor = new BluetoothGattDescriptor(
            UUID_IAQ_CLIENT_CONFIG,
            BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE
        );

        temperature.addDescriptor(temperatureConfigDescriptor);
        pressure.addDescriptor(pressureConfigDescriptor);
        humidity.addDescriptor(humidityConfigDescriptor);
        motion.addDescriptor(motionConfigDescriptor);
        iaq.addDescriptor(iaqConfigDescriptor);

        // Add characteristics to service
        service.addCharacteristic(temperature);
        service.addCharacteristic(pressure);
        service.addCharacteristic(humidity);
        service.addCharacteristic(motion);
        service.addCharacteristic(iaq);

        return service;
    }

    public static byte[] toByteArray(final float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public static byte[] toByteArray(final float[] values) {
        final ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (final float value : values) {
            buffer.putFloat(value);
        }

        return buffer.array();
    }
}
package com.knobtviker.thermopile.presentation.utils.predicates;

import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;

import java.util.function.Predicate;

public class PeripheralDevicePredicate implements Predicate<PeripheralDevice> {

    private final Sensor sensor;

    public static PeripheralDevicePredicate allowed(@NonNull final Sensor sensor) {
        return new PeripheralDevicePredicate(sensor);
    }

    private PeripheralDevicePredicate(@NonNull final Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public boolean test(PeripheralDevice peripheralDevice) {
        final boolean isSensorVendorAndName = sensor.getVendor().equalsIgnoreCase(peripheralDevice.vendor) && sensor.getName().equalsIgnoreCase(peripheralDevice.name);
        final boolean isConnected = peripheralDevice.connected;

        boolean hasAbilityAndEnabled = false;
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                hasAbilityAndEnabled = peripheralDevice.hasTemperature && peripheralDevice.enabledTemperature;
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                hasAbilityAndEnabled = peripheralDevice.hasHumidity && peripheralDevice.enabledHumidity;
                break;
            case Sensor.TYPE_PRESSURE:
                hasAbilityAndEnabled = peripheralDevice.hasPressure && peripheralDevice.enabledPressure;
                break;
            case Sensor.TYPE_LIGHT:
                hasAbilityAndEnabled = peripheralDevice.hasLuminosity && peripheralDevice.enabledLuminosity;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                hasAbilityAndEnabled = peripheralDevice.hasAcceleration && peripheralDevice.enabledAcceleration;
                break;
            case Sensor.TYPE_GYROSCOPE:
                hasAbilityAndEnabled = peripheralDevice.hasAngularVelocity && peripheralDevice.enabledAngularVelocity;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                hasAbilityAndEnabled = peripheralDevice.hasMagneticField && peripheralDevice.enabledMagneticField;
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    hasAbilityAndEnabled = peripheralDevice.hasAirQuality && peripheralDevice.enabledAirQuality;
                }
                break;
        }

        return isSensorVendorAndName && isConnected && hasAbilityAndEnabled;
    }
}

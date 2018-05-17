package com.knobtviker.thermopile.domain.utils.predicates;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;

import java.util.function.Predicate;

public class PeripheralDevicePredicate implements Predicate<PeripheralDevice> {

    private final String vendor;
    private final String name;

    public static PeripheralDevicePredicate allowed(@NonNull final String vendor, @NonNull final String name) {
        return new PeripheralDevicePredicate(vendor, name);
    }

    private PeripheralDevicePredicate(@NonNull final String vendor, @NonNull final String name) {
        this.vendor = vendor;
        this.name = name;
    }

    @Override
    public boolean test(PeripheralDevice peripheralDevice) {
        final boolean isSensorVendorAndName = this.vendor.equalsIgnoreCase(peripheralDevice.vendor) && this.name.equalsIgnoreCase(peripheralDevice.name);
        final boolean isConnected = peripheralDevice.connected;

        boolean hasAbilityAndEnabled = false;
//        switch (sensor.getType()) {
//            case Sensor.TYPE_AMBIENT_TEMPERATURE:
//                hasAbilityAndEnabled = peripheralDevice.hasTemperature && peripheralDevice.enabledTemperature;
//                break;
//            case Sensor.TYPE_RELATIVE_HUMIDITY:
//                hasAbilityAndEnabled = peripheralDevice.hasHumidity && peripheralDevice.enabledHumidity;
//                break;
//            case Sensor.TYPE_PRESSURE:
//                hasAbilityAndEnabled = peripheralDevice.hasPressure && peripheralDevice.enabledPressure;
//                break;
//            case Sensor.TYPE_LIGHT:
//                hasAbilityAndEnabled = peripheralDevice.hasLuminosity && peripheralDevice.enabledLuminosity;
//                break;
//            case Sensor.TYPE_ACCELEROMETER:
//                hasAbilityAndEnabled = peripheralDevice.hasAcceleration && peripheralDevice.enabledAcceleration;
//                break;
//            case Sensor.TYPE_GYROSCOPE:
//                hasAbilityAndEnabled = peripheralDevice.hasAngularVelocity && peripheralDevice.enabledAngularVelocity;
//                break;
//            case Sensor.TYPE_MAGNETIC_FIELD:
//                hasAbilityAndEnabled = peripheralDevice.hasMagneticField && peripheralDevice.enabledMagneticField;
//                break;
//            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
//                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
//                    hasAbilityAndEnabled = peripheralDevice.hasAirQuality && peripheralDevice.enabledAirQuality;
//                }
//                break;
//        }

        return isSensorVendorAndName && isConnected && hasAbilityAndEnabled;
    }
}

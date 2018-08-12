package com.knobtviker.thermopile.shared.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    Keys.UID,
    Keys.VENDOR,
    Keys.NAME,
    Keys.VALUE,
    Keys.LAST_BOOT_TIMESTAMP,
    Keys.BOOT_COUNT,
    Keys.TEMPERATURE,
    Keys.PRESSURE,
    Keys.HUMIDITY,
    Keys.AIR_QUALITY,
    Keys.LUMINOSITY,
    Keys.ACCELERATION,
    Keys.ANGULAR_VELOCITY,
    Keys.MAGNETIC_FIELD,
    Keys.HAS_BLUETOOTH,
    Keys.HAS_BLUETOOTH_LE,
    Keys.IS_BLUETOOTH_ENABLED,
    Keys.IS_GATT_RUNNING,
    Keys.IS_ADVERTISING,
    Keys.BLUETOOTH_STATE
})
public @interface Keys {
    String UID = "uid";
    String VENDOR = "vendor";
    String NAME = "name";
    String VALUE = "value";
    String LAST_BOOT_TIMESTAMP = "last_boot_timestamp";
    String BOOT_COUNT = "boot_count";
    String TEMPERATURE = "temperature";
    String PRESSURE = "pressure";
    String HUMIDITY = "humidity";
    String AIR_QUALITY = "air_quality";
    String LUMINOSITY = "luminosity";
    String ACCELERATION = "acceleration";
    String ANGULAR_VELOCITY = "angular_velocity";
    String MAGNETIC_FIELD = "magnetic_field";
    String HAS_BLUETOOTH = "has_bluetooth";
    String HAS_BLUETOOTH_LE = "has_bluetooth_le";
    String IS_BLUETOOTH_ENABLED = "is_bluetooth_enabled";
    String IS_GATT_RUNNING = "is_gatt_running";
    String IS_ADVERTISING = "is_advertising";
    String BLUETOOTH_STATE = "bluetooth_state";
}

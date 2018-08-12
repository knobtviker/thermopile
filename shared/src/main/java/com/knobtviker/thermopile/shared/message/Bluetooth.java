package com.knobtviker.thermopile.shared.message;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Bluetooth.HAS_BLUETOOTH,
    Bluetooth.HAS_BLUETOOTH_LE,
    Bluetooth.IS_ENABLED,
    Bluetooth.IS_GATT_RUNNING,
    Bluetooth.IS_ADVERTISING,
    Bluetooth.STATE,
})
public @interface Bluetooth {
    int HAS_BLUETOOTH = 0;
    int HAS_BLUETOOTH_LE = 1;
    int IS_ENABLED = 2;
    int IS_GATT_RUNNING = 3;
    int IS_ADVERTISING = 4;
    int STATE = 5;
}

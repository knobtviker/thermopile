package com.knobtviker.thermopile.shared.constants;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    BluetoothState.OFF,
    BluetoothState.TURNING_ON,
    BluetoothState.ON,
    BluetoothState.TURNING_OFF
})
public @interface BluetoothState {

    int OFF = BluetoothAdapter.STATE_OFF;
    int TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;
    int ON = BluetoothAdapter.STATE_ON;
    int TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
}

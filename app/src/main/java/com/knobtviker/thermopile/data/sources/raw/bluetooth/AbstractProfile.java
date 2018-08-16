package com.knobtviker.thermopile.data.sources.raw.bluetooth;

import android.bluetooth.BluetoothGattService;

public abstract class AbstractProfile {

    public abstract BluetoothGattService createService();
}

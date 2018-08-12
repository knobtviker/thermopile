package com.knobtviker.thermopile.presentation.views.communicators;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.shared.constants.BluetoothState;

public interface IncomingCommunicator {

    void saveTemperature(@NonNull final String vendor, @NonNull final String name, final float value);

    void savePressure(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveHumidity(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveAirQuality(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveLuminosity(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveAcceleration(@NonNull final String vendor, @NonNull final String name, final float[] values);

    void saveAngularVelocity(@NonNull final String vendor, @NonNull final String name, final float[] values);

    void saveMagneticField(@NonNull final String vendor, @NonNull final String name, final float[] values);

    void setLastBootTimestamp(final long value);

    void setBootCount(final long value);

    void setHasBluetooth(final boolean value);

    void setBluetoothEnabled(final boolean value);

    void setBluetoothState(@BluetoothState final int value);
}

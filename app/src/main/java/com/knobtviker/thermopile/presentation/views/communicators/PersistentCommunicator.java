package com.knobtviker.thermopile.presentation.views.communicators;

import android.support.annotation.NonNull;

public interface PersistentCommunicator {

    void saveTemperature(@NonNull final String vendor, @NonNull final String name, final float value);

    void savePressure(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveHumidity(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveAirQuality(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveLuminosity(@NonNull final String vendor, @NonNull final String name, final float value);

    void saveAcceleration(@NonNull final String vendor, @NonNull final String name, final float[] values);

    void saveAngularVelocity(@NonNull final String vendor, @NonNull final String name, final float[] values);

    void saveMagneticField(@NonNull final String vendor, @NonNull final String name, final float[] values);
}

package com.knobtviker.thermopile.shared.message;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Type.TEMPERATURE,
    Type.PRESSURE,
    Type.HUMIDITY,
    Type.AIR_QUALITY,
    Type.LUMINOSITY,
    Type.ACCELERATION,
    Type.ANGULAR_VELOCITY,
    Type.MAGNETIC_FIELD
})
public @interface Type {
    int TEMPERATURE = 0;
    int PRESSURE = 1;
    int HUMIDITY = 2;
    int AIR_QUALITY = 3;
    int LUMINOSITY = 4;
    int ACCELERATION = 5;
    int ANGULAR_VELOCITY = 6;
    int MAGNETIC_FIELD = 7;
}

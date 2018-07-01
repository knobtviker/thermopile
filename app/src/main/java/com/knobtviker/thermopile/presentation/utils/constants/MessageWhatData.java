package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    MessageWhatData.CURRENT,
    MessageWhatData.TEMPERATURE,
    MessageWhatData.PRESSURE,
    MessageWhatData.HUMIDITY,
    MessageWhatData.AIR_QUALITY,
    MessageWhatData.LUMINOSITY,
    MessageWhatData.ACCELERATION,
    MessageWhatData.ANGULAR_VELOCITY,
    MessageWhatData.MAGNETIC_FIELD
})
public @interface MessageWhatData {
    int CURRENT = -1;
    int TEMPERATURE = 0;
    int PRESSURE = 1;
    int HUMIDITY = 2;
    int AIR_QUALITY = 3;
    int LUMINOSITY = 4;
    int ACCELERATION = 5;
    int ANGULAR_VELOCITY = 6;
    int MAGNETIC_FIELD = 7;
}

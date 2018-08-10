package com.knobtviker.thermopile.presentation.shared.constants.charts;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    ChartType.TEMPERATURE,
    ChartType.HUMIDITY,
    ChartType.PRESSURE,
    ChartType.AIR_QUALITY,
    ChartType.MOTION
})
public @interface ChartType {
    int TEMPERATURE = 0;
    int HUMIDITY = 1;
    int PRESSURE = 2;
    int AIR_QUALITY = 3;
    int MOTION = 4;
}

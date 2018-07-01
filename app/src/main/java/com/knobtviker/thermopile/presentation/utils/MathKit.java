package com.knobtviker.thermopile.presentation.utils;

import com.knobtviker.thermopile.presentation.utils.constants.UnitAcceleration;
import com.knobtviker.thermopile.presentation.utils.constants.UnitPressure;
import com.knobtviker.thermopile.presentation.utils.constants.UnitTemperature;

/**
 * Created by bojan on 10/08/2017.
 */

public class MathKit {

    public static int round(final float decimal) {
        return Math.round(decimal);
    }

    public static float roundToOne(final float decimal) {
        return (float) (Math.round(decimal * 10.0) / 10.0);
    }

    public static float applyTemperatureUnit(@UnitTemperature final int unit, final float measuredTemperature) {
        switch (unit) {
            case UnitTemperature.CELSIUS:
                return measuredTemperature * 1.0f;
            case UnitTemperature.FAHRENHEIT:
                return measuredTemperature * 1.8f + 32.0f;
            case UnitTemperature.KELVIN:
                return measuredTemperature + 273.15f;
            default:
                return measuredTemperature * 1.0f;
        }
    }

    public static float applyPressureUnit(@UnitPressure final int unit, final float measuredPressure) {
        switch (unit) {
            case UnitPressure.PASCAL:
                return measuredPressure * 1.0f; //in hectopascals
            case UnitPressure.BAR:
                return measuredPressure * 1.0f; //in milibars
            case UnitPressure.PSI:
                return measuredPressure * 0.014503773773022f; //in psi
            default:
                return measuredPressure * 1.0f;
        }
    }

    public static float applyAccelerationUnit(@UnitAcceleration final int unit, final float measuredAcceleration) {
        switch (unit) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                return measuredAcceleration * 1.0f; //in m/s2
            case UnitAcceleration.G:
                return measuredAcceleration * 0.101971621f; //in g
            default:
                return measuredAcceleration * 1.0f;
        }
    }
}

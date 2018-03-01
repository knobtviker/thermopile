package com.knobtviker.thermopile.presentation.utils;

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

    public static float applyTemperatureUnit(final int unit, final float measuredTemperature) {
        switch (unit) {
            case Constants.UNIT_TEMPERATURE_CELSIUS:
                return measuredTemperature * 1.0f;
            case Constants.UNIT_TEMPERATURE_FAHRENHEIT:
                return measuredTemperature * 1.8f + 32.0f;
            case Constants.UNIT_TEMPERATURE_KELVIN:
                return measuredTemperature + 273.15f;
            default:
                return measuredTemperature * 1.0f;
        }
    }

    public static float applyPressureUnit(final int unit, final float measuredPressure) {
        switch (unit) {
            case Constants.UNIT_PRESSURE_PASCAL:
                return measuredPressure * 1.0f; //in hectopascals
            case Constants.UNIT_PRESSURE_BAR:
                return measuredPressure * 1.0f; //in milibars
            case Constants.UNIT_PRESSURE_PSI:
                return measuredPressure * 0.014503773773022f; //in psi
            default:
                return measuredPressure * 1.0f;
        }
    }

    public static float applyAccelerationUnit(final int unit, final float measuredAcceleration) {
        switch (unit) {
            case Constants.UNIT_ACCELERATION_METERS_PER_SECOND_2:
                return measuredAcceleration * 1.0f; //in m/s2
            case Constants.UNIT_ACCELERATION_G:
                return measuredAcceleration * 0.101971621f; //in g
            default:
                return measuredAcceleration * 1.0f;
        }
    }
}

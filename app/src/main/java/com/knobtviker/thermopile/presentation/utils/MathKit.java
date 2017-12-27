package com.knobtviker.thermopile.presentation.utils;

/**
 * Created by bojan on 10/08/2017.
 */

public class MathKit {

    public static int round(final float decimal) {
        return Math.round(decimal);
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
}

package com.knobtviker.thermopile.presentation.utils;

/**
 * Created by bojan on 15/06/2017.
 */

public class Constants {
    public static final int REQUEST_CODE_BLUETOOTH_DISCOVERABILITY = 8008;

    public static final String KEY_DAY = "key_day";
    public static final String KEY_START_MINUTE = "key_start_minute";
    public static final String KEY_MAX_WIDTH = "key_max_width";
    public static final String KEY_THRESHOLD_ID = "key_threshold_id";

    public static final float MEASURED_TEMPERATURE_MIN = 5.0f;
    public static final float MEASURED_TEMPERATURE_MAX = 35.0f;
    public static final float MEASURED_HUMIDITY_MAX = 100.0f;
    public static final float MEASURED_PRESSURE_MAX = 1100.0f;

    public static final int CLOCK_MODE_12H = 0;
    public static final int CLOCK_MODE_24H = 1;

    public static final int UNIT_TEMPERATURE_CELSIUS = 0;
    public static final int UNIT_TEMPERATURE_FAHRENHEIT = 1;
    public static final int UNIT_TEMPERATURE_KELVIN = 2;

    public static final int UNIT_PRESSURE_PASCAL = 0;
    public static final int UNIT_PRESSURE_BAR = 1;
    public static final int UNIT_PRESSURE_PSI = 2;

    public static final int UNIT_ACCELERATION_METERS_PER_SECOND_2 = 0;
    public static final int UNIT_ACCELERATION_G = 1;

    public static final String FORMAT_DAY_LONG = "EEEE";
    public static final String FORMAT_DAY_SHORT = "EE";
    public static final String FORMAT_TIME_LONG_24H = "HH:mm";
    public static final String FORMAT_TIME_SHORT_24H = "H:m";
    public static final String FORMAT_TIME_LONG_12H = "KK:mm a";
    public static final String FORMAT_TIME_SHORT_12H = "K:m a";

    public static final String DEFAULT_TIMEZONE = "Europe/Zagreb";
    public static final String DEFAULT_FORMAT_DATE = String.format("%s dd.MM.yyyy.", FORMAT_DAY_LONG);

    public static final int DEFAULT_SCREENSAVER_DELAY = 60;

    public static final int TYPE_TEMPERATURE = 0;
    public static final int TYPE_PRESSURE = 1;
    public static final int TYPE_HUMIDITY = 2;
    public static final int TYPE_AIR_QUALITY = 3;
    public static final int TYPE_LUMINOSITY = 4;
    public static final int TYPE_ACCELERATION = 5;
    public static final int TYPE_ANGULAR_VELOCITY = 6;
    public static final int TYPE_MAGNETIC_FIELD = 7;
}

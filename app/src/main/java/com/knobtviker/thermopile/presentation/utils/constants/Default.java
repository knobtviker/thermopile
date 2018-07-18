package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.v7.app.AppCompatDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Default {
    String TIMEZONE = "Europe/Zagreb";
    int CLOCK_MODE = ClockMode._24H;
    String FORMAT_DATE = FormatDate.EEEE_DD_MM_YYYY;
    String FORMAT_TIME = FormatTime.HH_MM;
    int UNIT_TEMPERATURE = UnitTemperature.CELSIUS;
    int UNIT_PRESSURE = UnitPressure.PASCAL;
    int UNIT_ACCELERATION = UnitAcceleration.METERS_PER_SECOND_2;
    int SCREENSAVER_DELAY = ScreensaverTimeout._1MIN;
    int THEME = AppCompatDelegate.MODE_NIGHT_AUTO;
    long INVALID_ID = -1L;
    int INVALID_WIDTH = -1;
}

package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.v7.app.AppCompatDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Default {
    String TIMEZONE = "Europe/Zagreb";
    int SCREENSAVER_DELAY = 60;
    int THEME = AppCompatDelegate.MODE_NIGHT_AUTO;
}

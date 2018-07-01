package com.knobtviker.thermopile.presentation.utils.constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Default {
    String TIMEZONE = "Europe/Zagreb";
    int SCREENSAVER_DELAY = 60;
}

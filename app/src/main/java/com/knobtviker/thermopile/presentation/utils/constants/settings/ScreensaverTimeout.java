package com.knobtviker.thermopile.presentation.utils.constants.settings;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    ScreensaverTimeout._15S,
    ScreensaverTimeout._30S,
    ScreensaverTimeout._1MIN,
    ScreensaverTimeout._2MIN,
    ScreensaverTimeout._5MIN,
    ScreensaverTimeout._10MIN
})
public @interface ScreensaverTimeout {
    int _15S = 15;
    int _30S = 30;
    int _1MIN = 60;
    int _2MIN = 120;
    int _5MIN = 300;
    int _10MIN = 600;
}

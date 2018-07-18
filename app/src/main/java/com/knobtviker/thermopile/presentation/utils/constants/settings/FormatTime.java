package com.knobtviker.thermopile.presentation.utils.constants.settings;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    FormatTime.HH_MM,
    FormatTime.H_M,
    FormatTime.KK_MM_A,
    FormatTime.K_M_A
})
public @interface FormatTime {

    String HH_MM = "HH:mm";
    String H_M = "H:m";
    String KK_MM_A = "KK:mm a";
    String K_M_A = "K:m a";
}

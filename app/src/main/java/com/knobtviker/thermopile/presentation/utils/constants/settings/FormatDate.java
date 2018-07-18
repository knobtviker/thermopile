package com.knobtviker.thermopile.presentation.utils.constants.settings;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    FormatDate.EEEE_DD_MM_YYYY,
    FormatDate.EE_DD_MM_YYYY,
    FormatDate.DD_MM_YYYY,
    FormatDate.EEEE_MM_DD_YYYY,
    FormatDate.EE_MM_DD_YYYY,
    FormatDate.MM_DD_YYYY
})
public @interface FormatDate {

    String EEEE_DD_MM_YYYY = "EEEE dd.MM.yyyy";
    String EE_DD_MM_YYYY = "EE dd.MM.yyyy";
    String DD_MM_YYYY = "dd.MM.yyyy";
    String EEEE_MM_DD_YYYY = "EEEE MM/dd/yyyy";
    String EE_MM_DD_YYYY = "EE MM/dd/yyyy";
    String MM_DD_YYYY = "MM/dd/yyyy";
}

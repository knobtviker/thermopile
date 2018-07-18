package com.knobtviker.thermopile.presentation.utils.constants.charts;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    ChartInterval.TODAY,
    ChartInterval.YESTERDAY,
    ChartInterval.THIS_WEEK,
    ChartInterval.LAST_WEEK,
    ChartInterval.THIS_MONTH,
    ChartInterval.LAST_MONTH,
    ChartInterval.THIS_YEAR,
    ChartInterval.LAST_YEAR
})
public @interface ChartInterval {
    int TODAY = 0;
    int YESTERDAY = 1;
    int THIS_WEEK = 2;
    int LAST_WEEK = 3;
    int THIS_MONTH = 4;
    int LAST_MONTH = 5;
    int THIS_YEAR = 6;
    int LAST_YEAR = 7;
}

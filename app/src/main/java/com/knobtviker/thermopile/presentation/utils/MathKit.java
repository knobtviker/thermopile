package com.knobtviker.thermopile.presentation.utils;

import java.math.BigDecimal;

/**
 * Created by bojan on 10/08/2017.
 */

public class MathKit {

    public static BigDecimal round(final float decimal, final int decimalPlace) {
        BigDecimal bigDecimal = new BigDecimal(Float.toString(decimal));
        bigDecimal = bigDecimal.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bigDecimal;
    }
}

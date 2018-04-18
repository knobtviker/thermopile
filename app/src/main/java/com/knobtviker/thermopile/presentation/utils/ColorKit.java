package com.knobtviker.thermopile.presentation.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;

public class ColorKit {

    public static int colorFromString(@NonNull final String value) {
        return Color.parseColor(value);
    }

    public static String colorToString(final int value) {
        return String.format("#%06X", 0xFFFFFF & value);
    }
}

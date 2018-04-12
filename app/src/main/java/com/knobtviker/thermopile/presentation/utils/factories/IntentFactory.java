package com.knobtviker.thermopile.presentation.utils.factories;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.utils.Constants;

public class IntentFactory {

    public static Intent temperature(@NonNull final Context context, final float value) {
        return generic(context, Constants.ACTION_NEW_TEMPERATURE, Constants.KEY_TEMPERATURE, value);
    }

    public static Intent pressure(@NonNull final Context context, final float value) {
        return generic(context, Constants.ACTION_NEW_PRESSURE, Constants.KEY_PRESSURE, value);
    }

    public static Intent altitude(@NonNull final Context context, final float value) {
        return generic(context, Constants.ACTION_NEW_ALTITUDE, Constants.KEY_ALTITUDE, value);
    }

    public static Intent humidity(@NonNull final Context context, final float value) {
        return generic(context, Constants.ACTION_NEW_HUMIDITY, Constants.KEY_HUMIDITY, value);
    }

    public static Intent airQuality(@NonNull final Context context, final float value) {
        return generic(context, Constants.ACTION_NEW_AIR_QUALITY, Constants.KEY_AIR_QUALITY, value);
    }

    public static Intent luminosity(@NonNull final Context context, final float value) {
        return generic(context, Constants.ACTION_NEW_LUMINOSITY, Constants.KEY_LUMINOSITY, value);
    }

    public static Intent acceleration(@NonNull final Context context, final float[] values) {
        return generic(context, Constants.ACTION_NEW_ACCELERATION, Constants.KEY_ACCELERATION, values);
    }

    public static Intent angularVelocity(@NonNull final Context context, final float[] values) {
        return generic(context, Constants.ACTION_NEW_ANGULAR_VELOCITY, Constants.KEY_ANGULAR_VELOCITY, values);
    }

    public static Intent magneticField(@NonNull final Context context, final float[] values) {
        return generic(context, Constants.ACTION_NEW_MAGNETIC_FIELD, Constants.KEY_MAGNETIC_FIELD, values);
    }

    private static Intent generic(@NonNull final Context context, @NonNull final String action, @NonNull final String key, final float value) {
        return new Intent(String.format("%s.%s", context.getPackageName(), action))
            .putExtra(key, value);
    }

    private static Intent generic(@NonNull final Context context, @NonNull final String action, @NonNull final String key, final float[] values) {
        return new Intent(String.format("%s.%s", context.getPackageName(), action))
            .putExtra(key, values);
    }
}

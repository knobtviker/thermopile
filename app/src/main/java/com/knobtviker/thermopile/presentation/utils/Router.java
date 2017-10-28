package com.knobtviker.thermopile.presentation.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;
import com.knobtviker.thermopile.presentation.activities.ThresholdActivity;

/**
 * Created by bojan on 29/07/2017.
 */

public class Router {

    public static void showScreensaver(@NonNull final Context context) {
        final Intent intent = new Intent(context, ScreenSaverActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    public static  void showThreshold(@NonNull final Context context, final int day, final int startMinute, final int maxWidth) {
        final Intent intent = new Intent(context, ThresholdActivity.class);

        intent.putExtra(Constants.KEY_DAY, day);
        intent.putExtra(Constants.KEY_START_MINUTE, startMinute);
        intent.putExtra(Constants.KEY_MAX_WIDTH, maxWidth);

        context.startActivity(intent);
    }

    public static void showThreshold(@NonNull final Context context, final long thresholdId) {
        final Intent intent = new Intent(context, ThresholdActivity.class);

        intent.putExtra(Constants.KEY_THRESHOLD_ID, thresholdId);

        context.startActivity(intent);
    }
}

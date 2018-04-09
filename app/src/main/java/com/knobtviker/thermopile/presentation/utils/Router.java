package com.knobtviker.thermopile.presentation.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.knobtviker.thermopile.presentation.activities.BootAnimationActivity;
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

    public static void showThreshold(@NonNull final Context context, final int day, final int startMinute, final int maxWidth) {
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

    public static void restart(@NonNull final Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ((AppCompatActivity) (context)).finish();
        context.startActivity(intent);
        System.exit(0);
    }

    public static void bootAnimation(@NonNull final Context context) {
        context.startActivity(new Intent(context, BootAnimationActivity.class));
    }
}

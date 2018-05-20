package com.knobtviker.thermopile.presentation.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

/**
 * Created by bojan on 29/07/2017.
 */

public class Router {

    public static void showScreensaver(@NonNull final Context context) {
        final Intent intent = new Intent(context, ScreenSaverActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
        ((AppCompatActivity)context).overridePendingTransition(R.anim.enter_top_to_bottom, R.anim.no_anim);
    }

    public static void restart(@NonNull final Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ((AppCompatActivity) (context)).finish();
        context.startActivity(intent);
        System.exit(0);
    }
}

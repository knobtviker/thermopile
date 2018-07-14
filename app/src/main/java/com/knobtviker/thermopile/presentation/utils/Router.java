package com.knobtviker.thermopile.presentation.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

/**
 * Created by bojan on 29/07/2017.
 */

public class Router {

    public static void showScreensaver(@NonNull final Context context) {
        final Intent intent = new Intent(context, ScreenSaverActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
//        ((AppCompatActivity)context).overridePendingTransition(R.anim.enter_top_to_bottom, R.anim.no_anim);
    }
}

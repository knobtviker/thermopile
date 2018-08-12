package com.knobtviker.thermopile.presentation.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

/**
 * Created by bojan on 29/07/2017.
 */

public class Router {

    public final static int MAX_DISCOVERABILITY_PERIOD_SECONDS = 300;

    public static void showScreensaver(@NonNull final Context context) {
        final Intent intent = new Intent(context, ScreenSaverActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
        //        ((AppCompatActivity)context).overridePendingTransition(R.anim.enter_top_to_bottom, R.anim.exit_bottom_to_top);
    }

    /**
     * This will issue a request to make the local device discoverable to other devices. By default,
     * the device will become discoverable for 120 seconds.
     *
     * @param context Context
     */
    public static void enableDiscoverability(@NonNull final Context context) {
        enableDiscoverability(context, -1);
    }

    /**
     * This will issue a request to make the local device discoverable to other devices. By default,
     * the device will become discoverable for 120 seconds.  Maximum duration is capped at 300
     * seconds.
     *
     * @param context  Context
     * @param duration discoverability duration in seconds
     */
    public static void enableDiscoverability(@NonNull final Context context, final int duration) {
        final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        if (duration >= 0) {
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                duration >= MAX_DISCOVERABILITY_PERIOD_SECONDS ? MAX_DISCOVERABILITY_PERIOD_SECONDS : duration);
        }
        context.startActivity(intent);
    }
}

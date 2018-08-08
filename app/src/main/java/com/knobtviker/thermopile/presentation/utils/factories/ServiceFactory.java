package com.knobtviker.thermopile.presentation.utils.factories;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Pair;

public class ServiceFactory {

    private final static Pair<String, String> DRIVERS = Pair.create("drivers", "DriversService");
    private final static Pair<String, String> FRAM = Pair.create("fram", "Mb85rc256vService");
    private final static Pair<String, String> BLUETOOTH = Pair.create("bluetooth", "BluetoothService");

    public static Intent drivers(@NonNull final Context context) {
        return new Intent()
            .setComponent(component(context, DRIVERS.first, DRIVERS.second));
    }

    public static Intent fram(@NonNull final Context context) {
        return new Intent()
            .setComponent(component(context, FRAM.first, FRAM.second));
    }

    public static Intent bluetooth(@NonNull final Context context) {
        return new Intent()
            .setComponent(component(context, BLUETOOTH.first, BLUETOOTH.second));
    }

    public static String packageNameDrivers(@NonNull final Context context) {
        return packageName(context, DRIVERS.first);
    }

    public static String packageNameFram(@NonNull final Context context) {
        return packageName(context, FRAM.first);
    }

    public static String packageNameBluetooth(@NonNull final Context context) {
        return packageName(context, BLUETOOTH.first);
    }

    private static String packageName(@NonNull final Context context, @NonNull final String module) {
        return String.format("%s.%s", context.getPackageName(), module);
    }

    private static ComponentName component(@NonNull final Context context, @NonNull final String module, @NonNull final String className) {
        return new ComponentName(packageName(context, module), String.format("%s.%s", packageName(context, module), className));
    }
}

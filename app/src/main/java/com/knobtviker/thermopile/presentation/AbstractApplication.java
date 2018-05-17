package com.knobtviker.thermopile.presentation;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.FileLoggingTree;
import com.knobtviker.thermopile.presentation.utils.factories.IntentFactory;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public abstract class AbstractApplication<P extends BasePresenter> extends Application implements ServiceConnection {

    @Nullable
    private Messenger serviceMessenger = null;

    @NonNull
    private Messenger foregroundMessenger;

    @NonNull
    protected P presenter;

    @NonNull
    protected String packageName;

    @Override
    public void onCreate() {
        super.onCreate();

        packageName();

        plantTree();

        services();

        memoryClass();
        memoryInfo();

        initCrashlytics();
        initJodaTime();
        initDatabase();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (!Database.getInstance().isClosed()) {
            Database.getInstance().close();
        }

        Database.getInstance().deleteAllFiles();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Driver service connected
        Timber.i("onServiceConnected %s", name.flattenToString());
        serviceMessenger = new Messenger(service);

        final Message messageToService = Message.obtain(null, Constants.MESSAGE_WHAT_REGISTER);
        messageToService.replyTo = foregroundMessenger;
        try {
            serviceMessenger.send(messageToService);
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // Driver service disconnected ...restart?
        Timber.i("onServiceDisconnected %s", name.flattenToString());
        serviceMessenger = null;
    }

    public void refresh() {
        try {
            if (serviceMessenger != null) {
                final Message messageToService = Message.obtain(null, Constants.MESSAGE_WHAT_CURRENT);
                serviceMessenger.send(messageToService);
            }
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    private void packageName() {
        this.packageName = getPackageName();
    }

    private void plantTree() {
        Timber.plant(new FileLoggingTree(this));
    }

    private void services() {
        foregroundMessenger = new Messenger(new IncomingHandler(this));

        final Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.knobtviker.thermopile.drivers", "com.knobtviker.thermopile.drivers.DriversService"));
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    private void memoryClass() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final int memoryClass = activityManager.getMemoryClass();

        Timber.i("Memory class: %d", memoryClass); // 256MB for RPi3
    }

    public void memoryInfo() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        Timber.i("Memory info -> Available: %d Total: %d Threshold: %d Is low: %s", memoryInfo.availMem, memoryInfo.totalMem, memoryInfo.threshold, memoryInfo.lowMemory);
    }

    private void initCrashlytics() {
        if (!TextUtils.isEmpty(BuildConfig.KEY_FABRIC)) {
            Fabric.with(this, new Crashlytics());
        }
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void initDatabase() {
        Database.init(this);
    }

    public static class IncomingHandler extends Handler {

        private final LocalBroadcastManager localBroadcastManager;
        private final String packageName;

        public IncomingHandler(@NonNull final Context context) {
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
            packageName = context.getPackageName();
        }

        @Override
        public void handleMessage(Message message) {
            String vendor = "";
            String name = "";
            float value = 0.0f;
            float[] values = {0.0f, 0.0f, 0.0f};
            switch (message.what) {
                case Constants.MESSAGE_WHAT_TEMPERATURE:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.temperature(packageName, value));
                    Timber.i("Temperature: %s from %s %s", value, vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_PRESSURE:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.pressure(packageName, value));
                    Timber.i("Pressure: %s from %s %s", value, vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_HUMIDITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.humidity(packageName, value));
                    Timber.i("Humidity: %s from %s %s", value, vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_AIR_QUALITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.airQuality(packageName, value));
                    Timber.i("Air quality: %s from %s %s", value, vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_LUMINOSITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.luminosity(packageName, value));
//                    Timber.i("Luminosity: %s from %s %s", value, vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_ACCELERATION:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    localBroadcastManager.sendBroadcast(IntentFactory.acceleration(packageName, values));
                    Timber.i("Acceleration: %s %s %s from %s %s", values[0], values[1], values[2], vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_ANGULAR_VELOCITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    localBroadcastManager.sendBroadcast(IntentFactory.angularVelocity(packageName, values));
                    Timber.i("Angular velocity: %s %s %s from %s %s", values[0], values[1], values[2], vendor, name);
                    break;
                case Constants.MESSAGE_WHAT_MAGNETIC_FIELD:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    localBroadcastManager.sendBroadcast(IntentFactory.magneticField(packageName, values));
                    Timber.i("Magnetic field: %s %s %s from %s %s", values[0], values[1], values[2], vendor, name);
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }
}

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
import com.knobtviker.thermopile.presentation.views.communicators.PersistentCommunicator;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public abstract class AbstractApplication<P extends BasePresenter> extends Application implements ServiceConnection, PersistentCommunicator {

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

    @Override
    public void onBindingDied(ComponentName name) {
//        Timber.i("onBindingDied %s", name.flattenToString());
//        serviceMessenger = null;
//
//        services();
    }

    @Override
    public void saveTemperature(@NonNull String vendor, @NonNull String name, float value) {
        onNewTemperature(vendor, name, value);
    }

    @Override
    public void savePressure(@NonNull String vendor, @NonNull String name, float value) {
        onNewPressure(vendor, name, value);
    }

    @Override
    public void saveHumidity(@NonNull String vendor, @NonNull String name, float value) {
        onNewHumidity(vendor, name, value);
    }

    @Override
    public void saveAirQuality(@NonNull String vendor, @NonNull String name, float value) {
        onNewAirQuality(vendor, name, value);
    }

    @Override
    public void saveLuminosity(@NonNull String vendor, @NonNull String name, float value) {
        onNewLuminosity(vendor, name, value);
    }

    @Override
    public void saveAcceleration(@NonNull String vendor, @NonNull String name, float[] values) {
        onNewAcceleration(vendor, name, values);
    }

    @Override
    public void saveAngularVelocity(@NonNull String vendor, @NonNull String name, float[] values) {
        onNewAngularVelocity(vendor, name, values);
    }

    @Override
    public void saveMagneticField(@NonNull String vendor, @NonNull String name, float[] values) {
        onNewMagneticField(vendor, name, values);
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

    public abstract void onNewTemperature(@NonNull final String vendor, @NonNull final String name, final float value);

    public abstract void onNewPressure(@NonNull final String vendor, @NonNull final String name, final float value);

    public abstract void onNewHumidity(@NonNull final String vendor, @NonNull final String name, final float value);

    public abstract void onNewAirQuality(@NonNull final String vendor, @NonNull final String name, final float value);

    public abstract void onNewLuminosity(@NonNull final String vendor, @NonNull final String name, final float value);

    public abstract void onNewAcceleration(@NonNull final String vendor, @NonNull final String name, final float[] values);

    public abstract void onNewAngularVelocity(@NonNull final String vendor, @NonNull final String name, final float[] values);

    public abstract void onNewMagneticField(@NonNull final String vendor, @NonNull final String name, final float[] values);

    public static class IncomingHandler extends Handler {

        @NonNull
        private final LocalBroadcastManager localBroadcastManager;

        @NonNull
        private final String packageName;

        @NonNull
        private final PersistentCommunicator persistentCommunicator;

        IncomingHandler(@NonNull final Context context) {
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
            packageName = context.getPackageName();
            persistentCommunicator = (PersistentCommunicator) context;
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
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveTemperature(vendor, name, value);
                    }
                    break;
                case Constants.MESSAGE_WHAT_PRESSURE:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.pressure(packageName, value));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.savePressure(vendor, name, value);
                    }
                    break;
                case Constants.MESSAGE_WHAT_HUMIDITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.humidity(packageName, value));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveHumidity(vendor, name, value);
                    }
                    break;
                case Constants.MESSAGE_WHAT_AIR_QUALITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.airQuality(packageName, value));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveAirQuality(vendor, name, value);
                    }
                    break;
                case Constants.MESSAGE_WHAT_LUMINOSITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    localBroadcastManager.sendBroadcast(IntentFactory.luminosity(packageName, value));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveLuminosity(vendor, name, value);
                    }
                    break;
                case Constants.MESSAGE_WHAT_ACCELERATION:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    localBroadcastManager.sendBroadcast(IntentFactory.acceleration(packageName, values));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveAcceleration(vendor, name, values);
                    }
                    break;
                case Constants.MESSAGE_WHAT_ANGULAR_VELOCITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    localBroadcastManager.sendBroadcast(IntentFactory.angularVelocity(packageName, values));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveAngularVelocity(vendor, name, values);
                    }
                    break;
                case Constants.MESSAGE_WHAT_MAGNETIC_FIELD:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    localBroadcastManager.sendBroadcast(IntentFactory.magneticField(packageName, values));
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveMagneticField(vendor, name, values);
                    }
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }
}

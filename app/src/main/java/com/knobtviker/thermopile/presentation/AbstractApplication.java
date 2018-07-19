package com.knobtviker.thermopile.presentation;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.constants.messenger.MessageWhatData;
import com.knobtviker.thermopile.presentation.utils.constants.messenger.MessageWhatUser;
import com.knobtviker.thermopile.presentation.utils.factories.ServiceFactory;
import com.knobtviker.thermopile.presentation.views.communicators.PersistentCommunicator;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public abstract class AbstractApplication<P extends BasePresenter> extends Application implements ServiceConnection, PersistentCommunicator {

    @Nullable
    private Messenger serviceMessengerSensors = null;

    @Nullable
    private Messenger serviceMessengerFram = null;

    @NonNull
    private Messenger foregroundMessenger;

    @NonNull
    protected P presenter;

    @Override
    public void onCreate() {
        super.onCreate();

        plantTree();

        services();

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
        Timber.i("onServiceConnected %s", name.flattenToString());

        if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameDrivers(this))) { // Driver service connected
            serviceMessengerSensors = new Messenger(service);

            final Message messageToService = Message.obtain(null, MessageWhatUser.REGISTER);
            messageToService.replyTo = foregroundMessenger;
            try {
                serviceMessengerSensors.send(messageToService);
            } catch (RemoteException e) { //DeadObjectException
                Timber.e(e);
            }
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameFram(this))) { // FRAM service connected
            serviceMessengerFram = new Messenger(service);

            final Message messageToService = Message.obtain(null, MessageWhatUser.REGISTER);
            messageToService.replyTo = foregroundMessenger;
            try {
                serviceMessengerFram.send(messageToService);
            } catch (RemoteException e) { //DeadObjectException
                Timber.e(e);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.i("onServiceDisconnected %s", name.flattenToString());

        if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameDrivers(this))) { // Driver service disconnected
            serviceMessengerSensors = null;
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameFram(this))) { // FRAM service disconnected
            serviceMessengerFram = null;
        }
    }

    @Override
    public void onBindingDied(ComponentName name) {
//        Timber.i("onBindingDied %s", name.flattenToString());
//        serviceMessengerSensors = null;
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

    @Override
    public void setLastBootTimestamp(long value) {
        onLastBootTimestamp(value);
    }

    @Override
    public void setBootCount(long value) {
        onBootCount(value);
    }

    public void refresh() {
        try {
            if (serviceMessengerSensors != null) {
                final Message messageToService = Message.obtain(null, MessageWhatData.CURRENT);
                serviceMessengerSensors.send(messageToService);
            }
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    public void reset() {
        try {
            if (serviceMessengerFram != null) {
                final Message messageToService = Message.obtain(null, MessageWhatData.RESET);
                serviceMessengerFram.send(messageToService);
            }
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    private void plantTree() {
        Timber.plant(new Timber.DebugTree());
    }

    private void services() {
        foregroundMessenger = new Messenger(new IncomingHandler(this));

        bindService(ServiceFactory.drivers(this), this, BIND_AUTO_CREATE);
        bindService(ServiceFactory.fram(this), this, BIND_AUTO_CREATE);
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

    public abstract void onLastBootTimestamp(final long value);

    public abstract void onBootCount(final long value);

    public static class IncomingHandler extends Handler {

        @NonNull
        private final PersistentCommunicator persistentCommunicator;

        IncomingHandler(@NonNull final Context context) {
            persistentCommunicator = (PersistentCommunicator) context;
        }

        @Override
        public void handleMessage(Message message) {
            String vendor = "";
            String name = "";
            float value = 0.0f;
            float[] values = {0.0f, 0.0f, 0.0f};

            long lastBootTimestamp = 0L;
            long bootCount = 1l;

            switch (message.what) {
                case MessageWhatData.TEMPERATURE:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveTemperature(vendor, name, value);
                    }
                    break;
                case MessageWhatData.PRESSURE:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.savePressure(vendor, name, value);
                    }
                    break;
                case MessageWhatData.HUMIDITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveHumidity(vendor, name, value);
                    }
                    break;
                case MessageWhatData.AIR_QUALITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveAirQuality(vendor, name, value);
                    }
                    break;
                case MessageWhatData.LUMINOSITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    value = message.getData().getFloat("value");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveLuminosity(vendor, name, value);
                    }
                    break;
                case MessageWhatData.ACCELERATION:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveAcceleration(vendor, name, values);
                    }
                    break;
                case MessageWhatData.ANGULAR_VELOCITY:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveAngularVelocity(vendor, name, values);
                    }
                    break;
                case MessageWhatData.MAGNETIC_FIELD:
                    vendor = message.getData().getString("vendor");
                    name = message.getData().getString("name");
                    values = message.getData().getFloatArray("values");
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        persistentCommunicator.saveMagneticField(vendor, name, values);
                    }
                    break;
                case MessageWhatData.LAST_BOOT_TIMESTAMP:
                    lastBootTimestamp = message.getData().getLong("value");
                    persistentCommunicator.setLastBootTimestamp(lastBootTimestamp);
                    break;
                case MessageWhatData.BOOT_COUNT:
                    bootCount = message.getData().getLong("value");
                    persistentCommunicator.setBootCount(bootCount);
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }
}

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
import com.knobtviker.thermopile.presentation.utils.factories.ServiceFactory;
import com.knobtviker.thermopile.presentation.views.communicators.IncomingCommunicator;
import com.knobtviker.thermopile.shared.constants.Keys;
import com.knobtviker.thermopile.shared.constants.Uid;
import com.knobtviker.thermopile.shared.message.Action;
import com.knobtviker.thermopile.shared.message.Data;
import com.knobtviker.thermopile.shared.message.Type;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public abstract class AbstractApplication<P extends BasePresenter> extends Application implements ServiceConnection, IncomingCommunicator {

    @Nullable
    private Messenger serviceMessengerSensors = null;

    @Nullable
    private Messenger serviceMessengerFram = null;

    @Nullable
    private Messenger serviceMessengerBluetooth = null;

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

            final Message messageToService = Message.obtain(null, Action.REGISTER);
            messageToService.replyTo = foregroundMessenger;
            try {
                serviceMessengerSensors.send(messageToService);
            } catch (RemoteException e) { //DeadObjectException
                Timber.e(e);
            }
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameFram(this))) { // FRAM service connected
            serviceMessengerFram = new Messenger(service);

            final Message messageToService = Message.obtain(null, Action.REGISTER);
            messageToService.replyTo = foregroundMessenger;
            try {
                serviceMessengerFram.send(messageToService);
            } catch (RemoteException e) { //DeadObjectException
                Timber.e(e);
            }
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameBluetooth(this))) { // Bluetooth service connected
            serviceMessengerBluetooth = new Messenger(service);

            final Message messageToService = Message.obtain(null, Action.REGISTER);
            messageToService.replyTo = foregroundMessenger;
            try {
                serviceMessengerBluetooth.send(messageToService);
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
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameBluetooth(this))) { // FRAM service disconnected
            serviceMessengerBluetooth = null;
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
                final Message messageToService = Message.obtain(null, Action.CURRENT);
                serviceMessengerSensors.send(messageToService);
            }
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    public void reset() {
        try {
            if (serviceMessengerFram != null) {
                final Message messageToService = Message.obtain(null, Action.RESET);
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
        bindService(ServiceFactory.bluetooth(this), this, BIND_AUTO_CREATE);
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
        private final IncomingCommunicator incomingCommunicator;

        IncomingHandler(@NonNull final Context context) {
            incomingCommunicator = (IncomingCommunicator) context;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.getData().getInt(Keys.UID, Uid.INVALID)) {
                case Uid.DRIVERS:
                    handleDriverMessage(message);
                    break;
                case Uid.FRAM:
                    handleFramMessage(message);
                    break;
                case Uid.BLUETOOTH:
                    handleBluetoothMessage(message);
                    break;
                case Uid.INVALID:
                default:
                    super.handleMessage(message);
            }
        }

        private void handleDriverMessage(@NonNull final Message message) {
            String vendor = "";
            String name = "";
            float value = 0.0f;
            float[] values = {0.0f, 0.0f, 0.0f};

            switch (message.what) {
                case Type.TEMPERATURE:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    value = message.getData().getFloat(Keys.VALUE);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveTemperature(vendor, name, value);
                    }
                    break;
                case Type.PRESSURE:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    value = message.getData().getFloat(Keys.VALUE);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.savePressure(vendor, name, value);
                    }
                    break;
                case Type.HUMIDITY:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    value = message.getData().getFloat(Keys.VALUE);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveHumidity(vendor, name, value);
                    }
                    break;
                case Type.AIR_QUALITY:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    value = message.getData().getFloat(Keys.VALUE);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveAirQuality(vendor, name, value);
                    }
                    break;
                case Type.LUMINOSITY:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    value = message.getData().getFloat(Keys.VALUE);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveLuminosity(vendor, name, value);
                    }
                    break;
                case Type.ACCELERATION:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    values = message.getData().getFloatArray(Keys.VALUES);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveAcceleration(vendor, name, values);
                    }
                    break;
                case Type.ANGULAR_VELOCITY:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    values = message.getData().getFloatArray(Keys.VALUES);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveAngularVelocity(vendor, name, values);
                    }
                    break;
                case Type.MAGNETIC_FIELD:
                    vendor = message.getData().getString(Keys.VENDOR);
                    name = message.getData().getString(Keys.NAME);
                    values = message.getData().getFloatArray(Keys.VALUES);
                    if (!TextUtils.isEmpty(vendor) && !TextUtils.isEmpty(name)) {
                        incomingCommunicator.saveMagneticField(vendor, name, values);
                    }
                    break;
                default:
                    super.handleMessage(message);
            }
        }

        private void handleFramMessage(@NonNull final Message message) {
            long lastBootTimestamp = 0L;
            long bootCount = 1l;

            switch (message.what) {
                case Data.LAST_BOOT_TIMESTAMP:
                    lastBootTimestamp = message.getData().getLong(Keys.VALUE);
                    incomingCommunicator.setLastBootTimestamp(lastBootTimestamp);
                    break;
                case Data.BOOT_COUNT:
                    bootCount = message.getData().getLong(Keys.VALUE);
                    incomingCommunicator.setBootCount(bootCount);
                    break;
                default:
                    super.handleMessage(message);
            }
        }

        private void handleBluetoothMessage(@NonNull final Message message) {
//            long lastBootTimestamp = 0L;
//            long bootCount = 1l;
//
//            switch (message.what) {
//                case MessageWhatData.LAST_BOOT_TIMESTAMP:
//                    lastBootTimestamp = message.getData().getLong(Keys.VALUE);
//                    incomingCommunicator.setLastBootTimestamp(lastBootTimestamp);
//                    break;
//                case MessageWhatData.BOOT_COUNT:
//                    bootCount = message.getData().getLong(Keys.VALUE);
//                    incomingCommunicator.setBootCount(bootCount);
//                    break;
//                default:
//                    super.handleMessage(message);
//            }
        }
    }
}

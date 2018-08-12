package com.knobtviker.thermopile.presentation.shared.base;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.sources.local.shared.Database;
import com.knobtviker.thermopile.presentation.utils.factories.ServiceFactory;
import com.knobtviker.thermopile.presentation.views.communicators.IncomingCommunicator;
import com.knobtviker.thermopile.shared.MessageFactory;
import com.knobtviker.thermopile.shared.constants.BluetoothState;
import com.knobtviker.thermopile.shared.constants.Keys;
import com.knobtviker.thermopile.shared.constants.Uid;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Objects;

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
            MessageFactory.registerToBackground(foregroundMessenger, serviceMessengerSensors);
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameFram(this))) { // FRAM service connected
            serviceMessengerFram = new Messenger(service);
            MessageFactory.registerToBackground(foregroundMessenger, serviceMessengerFram);
        } else if (name.getPackageName().equalsIgnoreCase(ServiceFactory.packageNameBluetooth(this))) { // Bluetooth service connected
            serviceMessengerBluetooth = new Messenger(service);
            MessageFactory.registerToBackground(foregroundMessenger, serviceMessengerBluetooth);
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

    @Override
    public void setHasBluetooth(boolean value) {
        onHasBluetooh(value);
    }

    @Override
    public void setBluetoothEnabled(boolean value) {
        onBluetoothEnabled(value);
    }

    @Override
    public void setBluetoothState(int value) {
        onBluetoothState(value);
    }

    public void refresh() {
        if (serviceMessengerSensors != null) {
            MessageFactory.currentFromBackground(foregroundMessenger, serviceMessengerSensors);
        }
    }

    public void reset() {
        if (serviceMessengerFram != null) {
            MessageFactory.resetToBackground(foregroundMessenger, serviceMessengerFram);
        }
    }

    public void bluetoothEnable() {
        if (serviceMessengerBluetooth != null) {
            MessageFactory.bluetoothEnableToBackground(foregroundMessenger, serviceMessengerBluetooth);
        }
    }

    public void bluetoothDisable() {
        if (serviceMessengerBluetooth != null) {
            MessageFactory.bluetoothDisableToBackground(foregroundMessenger, serviceMessengerBluetooth);
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

    public abstract void onHasBluetooh(final boolean value);

    public abstract void onBluetoothEnabled(final boolean value);

    public abstract void onBluetoothState(@BluetoothState final int value);

    //TODO: All incoming handlers should be moved away from Application/Service classes and have a unified Message builder factory.
    public static class IncomingHandler extends Handler {

        @NonNull
        private final IncomingCommunicator incomingCommunicator;

        IncomingHandler(@NonNull final Context context) {
            incomingCommunicator = (IncomingCommunicator) context;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
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
            final Bundle data = message.getData();

            if (data.containsKey(Keys.VENDOR) && data.containsKey(Keys.NAME)) {
                if (data.containsKey(Keys.TEMPERATURE)) {
                    incomingCommunicator.saveTemperature(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloat(Keys.TEMPERATURE)
                    );
                }
                if (data.containsKey(Keys.PRESSURE)) {
                    incomingCommunicator.savePressure(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloat(Keys.PRESSURE)
                    );
                }
                if (data.containsKey(Keys.HUMIDITY)) {
                    incomingCommunicator.saveHumidity(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloat(Keys.HUMIDITY)
                    );
                }
                if (data.containsKey(Keys.AIR_QUALITY)) {
                    incomingCommunicator.saveAirQuality(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloat(Keys.AIR_QUALITY)
                    );
                }
                if (data.containsKey(Keys.LUMINOSITY)) {
                    incomingCommunicator.saveLuminosity(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloat(Keys.LUMINOSITY)
                    );
                }
                if (data.containsKey(Keys.ACCELERATION)) {
                    incomingCommunicator.saveAcceleration(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloatArray(Keys.ACCELERATION)
                    );
                }
                if (data.containsKey(Keys.ANGULAR_VELOCITY)) {
                    incomingCommunicator.saveAngularVelocity(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloatArray(Keys.ANGULAR_VELOCITY)
                    );
                }
                if (data.containsKey(Keys.MAGNETIC_FIELD)) {
                    incomingCommunicator.saveMagneticField(
                        Objects.requireNonNull(data.getString(Keys.VENDOR)),
                        Objects.requireNonNull(data.getString(Keys.NAME)),
                        data.getFloatArray(Keys.MAGNETIC_FIELD)
                    );
                }
            } else {
                super.handleMessage(message);
            }
        }

        private void handleFramMessage(@NonNull final Message message) {
            long lastBootTimestamp = 0L;
            long bootCount = 1l;

            final Bundle data = message.getData();
            if (data.containsKey(Keys.LAST_BOOT_TIMESTAMP)) {
                lastBootTimestamp = data.getLong(Keys.LAST_BOOT_TIMESTAMP);
                incomingCommunicator.setLastBootTimestamp(lastBootTimestamp);
            }
            if (data.containsKey(Keys.BOOT_COUNT)) {
                bootCount = data.getLong(Keys.BOOT_COUNT);
                incomingCommunicator.setBootCount(bootCount);
            }
        }

        private void handleBluetoothMessage(@NonNull final Message message) {
            final Bundle data = message.getData();

            if (data.containsKey(Keys.HAS_BLUETOOTH) && data.containsKey(Keys.HAS_BLUETOOTH_LE)) {
                incomingCommunicator.setHasBluetooth(data.getBoolean(Keys.HAS_BLUETOOTH) && data.getBoolean(Keys.HAS_BLUETOOTH_LE));
            }
            if (data.containsKey(Keys.IS_BLUETOOTH_ENABLED)) {
                incomingCommunicator.setBluetoothEnabled(data.getBoolean(Keys.IS_BLUETOOTH_ENABLED));
            }
            if (data.containsKey(Keys.IS_GATT_RUNNING)) {
//                bluetooth = bluetooth.withGattRunning(data.getBoolean(Keys.IS_GATT_RUNNING));
            }
            if (data.containsKey(Keys.IS_ADVERTISING)) {
//                bluetooth = bluetooth.withAdvertising(data.getBoolean(Keys.IS_ADVERTISING));
            }
            if (data.containsKey(Keys.BLUETOOTH_STATE)) {
                incomingCommunicator.setBluetoothState(data.getInt(Keys.BLUETOOTH_STATE));
            }
        }
    }
}

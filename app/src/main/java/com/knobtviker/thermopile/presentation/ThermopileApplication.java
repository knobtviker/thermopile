package com.knobtviker.thermopile.presentation;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.presentation.DaggerAppComponent;
import com.knobtviker.thermopile.di.modules.presentation.ApplicationModule;
import com.knobtviker.thermopile.di.qualifiers.presentation.messengers.ForegroundMessenger;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.knobtviker.thermopile.presentation.utils.factories.ServiceFactory;
import com.knobtviker.thermopile.presentation.views.communicators.IncomingCommunicator;
import com.knobtviker.thermopile.shared.MessageFactory;
import com.knobtviker.thermopile.shared.constants.Keys;
import com.knobtviker.thermopile.shared.constants.Uid;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.objectbox.BoxStore;
import timber.log.Timber;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApplication extends DaggerApplication implements ApplicationContract.View, ServiceConnection, IncomingCommunicator {

    private static long lastBootTimestamp = 0L;

    private static long bootCount = 1L;

    @Inject
    @ForegroundMessenger
    Messenger foregroundMessenger;

    //    @Inject
    //    @DriversMessenger
    Messenger serviceMessengerSensors;

    //    @Inject
    //    @FramMessenger
    Messenger serviceMessengerFram;

    @Inject
    BoxStore database;

    @Inject
    ApplicationContract.Presenter presenter;

    @Override
    public void onCreate() {
        super.onCreate();

        services();

        createScreensaver();
    }

    @Override
    protected AndroidInjector<ThermopileApplication> applicationInjector() {
        return DaggerAppComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    @Override
    public void onLowMemory() {
        if (!database.isClosed()) {
            database.close();
        }

        database.deleteAllFiles();

        super.onLowMemory();
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
        presenter.saveTemperature(vendor, name, value);
    }

    @Override
    public void savePressure(@NonNull String vendor, @NonNull String name, float value) {
        presenter.savePressure(vendor, name, value);
    }

    @Override
    public void saveHumidity(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveHumidity(vendor, name, value);
    }

    @Override
    public void saveAirQuality(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveAirQuality(vendor, name, value);
    }

    @Override
    public void saveLuminosity(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveLuminosity(vendor, name, value);
    }

    @Override
    public void saveAcceleration(@NonNull String vendor, @NonNull String name, float[] values) {
        presenter.saveAcceleration(vendor, name, values);
    }

    @Override
    public void saveAngularVelocity(@NonNull String vendor, @NonNull String name, float[] values) {
        presenter.saveAngularVelocity(vendor, name, values);
    }

    @Override
    public void saveMagneticField(@NonNull String vendor, @NonNull String name, float[] values) {
        presenter.saveMagneticField(vendor, name, values);
    }

    @Override
    public void setLastBootTimestamp(long value) {
        lastBootTimestamp = value;
    }

    @Override
    public void setBootCount(long value) {
        bootCount = value;
    }

    public void refresh() {
        if (foregroundMessenger != null && serviceMessengerSensors != null) {
            MessageFactory.currentFromBackground(foregroundMessenger, serviceMessengerSensors);
        }
    }

    public void reset() {
        if (foregroundMessenger != null && serviceMessengerFram != null) {
            MessageFactory.resetToBackground(foregroundMessenger, serviceMessengerFram);
        }
    }

    private void services() {
        bindService(ServiceFactory.drivers(this), this, BIND_AUTO_CREATE);
        bindService(ServiceFactory.fram(this), this, BIND_AUTO_CREATE);
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void showScreensaver() {
        Router.showScreensaver(this);
    }

    public static long lastBootTimestamp() {
        return lastBootTimestamp;
    }

    public static long bootCount() {
        return bootCount;
    }

    public void createScreensaver() {
        presenter.createScreensaver();
    }

    public void destroyScreensaver() {
        presenter.destroyScreensaver();
    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }

    //TODO: All incoming handlers should be moved away from Application/Service classes and have a unified Message builder factory.
    public static class IncomingHandler extends Handler {

        @NonNull
        private final IncomingCommunicator incomingCommunicator;

        public IncomingHandler(@NonNull final Context context) {
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
    }
}

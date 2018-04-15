package com.knobtviker.thermopile.presentation;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.bugfender.sdk.Bugfender;
import com.facebook.stetho.Stetho;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.FileLoggingTree;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;


public abstract class AbstractApplication<P extends BasePresenter> extends Application implements SensorEventListener {

    protected P presenter;

    @Override
    public void onCreate() {
        super.onCreate();

        plantTree();

        surveyMemory();
//        initCrashlytics();
        initBugfender();
        initJodaTime();
        initDatabase();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Database.getDefaultInstance().deleteAll();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                break;
            case Sensor.TYPE_PRESSURE:
                break;
            case Sensor.TYPE_LIGHT:
                break;
            case Sensor.TYPE_ACCELEROMETER:
                break;
            case Sensor.TYPE_GYROSCOPE:
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    // ...
                }
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                onTemperatureChanged(event);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                onHumidityChanged(event);
                break;
            case Sensor.TYPE_PRESSURE:
                onPressureChanged(event);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (event.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ) && event.sensor.getName().equals(Bme680.CHIP_NAME)) {
                    onAirQualityChanged(event);
                }
                break;
            case Sensor.TYPE_LIGHT:
                onLuminosityChanged(event);
                break;
            case Sensor.TYPE_ACCELEROMETER: //[m/s^2]
                onAccelerationChanged(event);
                break;
            case Sensor.TYPE_GYROSCOPE: //[°/s]
                onAngularVelocityChanged(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                onMagneticFieldChanged(event);
                break;
        }
    }

    public void registerSensorCallback() {
        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
                @Override
                public void onDynamicSensorConnected(Sensor sensor) {
                    registerListener(sensorManager, sensor);
                }
            });
        }
    }

    private void registerListener(@NonNull final SensorManager sensorManager, @NonNull final Sensor sensor) {
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
            case Sensor.TYPE_PRESSURE:
            case Sensor.TYPE_LIGHT:
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
        }
    }

    protected abstract void onTemperatureChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onPressureChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onHumidityChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onAirQualityChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onLuminosityChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onAccelerationChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onAngularVelocityChanged(@NonNull final SensorEvent sensorEvent);

    protected abstract void onMagneticFieldChanged(@NonNull final SensorEvent sensorEvent);

    private void plantTree() {
        Timber.plant(new FileLoggingTree(this));
    }

    private void surveyMemory() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final int memoryClass = activityManager.getMemoryClass();

        Timber.i("Memory class: %d", memoryClass); // 256MB for RPi3
    }

//    private void initCrashlytics() {
//        Fabric.with(this, new Crashlytics());
//    }

    private void initBugfender() {
        Bugfender.init(this, BuildConfig.KEY_BUGFENDER, BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableCrashReporting();
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void initDatabase() {
        Database.init(this);

        initStetho();
    }

    private void initStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                    RealmInspectorModulesProvider.builder(this)
                        .withLimit(1000)
                        .withDescendingOrder()
                        .build()
                )
                .build());
    }
}

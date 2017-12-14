package com.knobtviker.thermopile.presentation;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;

import com.facebook.stetho.Stetho;
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;
import com.google.android.things.device.ScreenManager;
import com.knobtviker.android.things.contrib.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.presentation.Accuracy;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApp extends Application implements SensorEventListener, ApplicationContract.View {
    private static final String TAG = ThermopileApp.class.getSimpleName();

    @NonNull
    private ApplicationContract.Presenter presenter;

    private Accuracy accuracy = Accuracy.EMPTY;

    private List<Float> temperatureBuffer = new ArrayList<>(Constants.BUFFER_SIZE);
    private List<Float> humidityBuffer = new ArrayList<>(Constants.BUFFER_SIZE);
    private List<Float> pressureBuffer = new ArrayList<>(Constants.BUFFER_SIZE);

//    private ScreenManager screenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        initScreen();
        initRealm();
//        initStetho();
        initCalligraphy();
        initJodaTime();
        initPresenter();
        initSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                if (temperatureBuffer.size() < Constants.BUFFER_SIZE) {
                    temperatureBuffer.add(sensorEvent.values[0]);
                }
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                if (humidityBuffer.size() < Constants.BUFFER_SIZE) {
                    humidityBuffer.add(sensorEvent.values[0]);
                }
                break;
            case Sensor.TYPE_PRESSURE:
//                Log.i(TAG, sensorEvent.values[0]+"");
                if (pressureBuffer.size() < Constants.BUFFER_SIZE) {
                    pressureBuffer.add(sensorEvent.values[0]);
                }
                break;
            case Sensor.TYPE_LIGHT:
                onLuminosityData(sensorEvent.values[0]);
                break;
        }

        if (temperatureBuffer.size() == Constants.BUFFER_SIZE && humidityBuffer.size() == Constants.BUFFER_SIZE && pressureBuffer.size() == Constants.BUFFER_SIZE) {
            presenter.saveData(
                BigDecimal
                    .valueOf(
                        temperatureBuffer
                            .stream()
                            .collect(Collectors.averagingDouble(n -> Double.parseDouble(Float.toString(n))))
                    )
                    .floatValue(),
                BigDecimal
                    .valueOf(
                        humidityBuffer
                            .stream()
                            .collect(Collectors.averagingDouble(n -> Double.parseDouble(Float.toString(n))))
                    )
                    .floatValue(),
                BigDecimal
                    .valueOf(
                        pressureBuffer
                            .stream()
                            .collect(Collectors.averagingDouble(n -> Double.parseDouble(Float.toString(n))))
                    )
                    .floatValue(),
                this.accuracy.temperature(),
                this.accuracy.humidity(),
                this.accuracy.pressure()
            );

            temperatureBuffer.clear();
            humidityBuffer.clear();
            pressureBuffer.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                this.accuracy = this.accuracy.withTemperature(accuracy);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                this.accuracy = this.accuracy.withHumidity(accuracy);
                break;
            case Sensor.TYPE_PRESSURE:
                this.accuracy = this.accuracy.withPressure(accuracy);
                break;
            case Sensor.TYPE_LIGHT:
                this.accuracy = this.accuracy.withLight(accuracy);
                break;
        }
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void showScreensaver() {
        Router.showScreensaver(this);
        final ScreenManager screenManager = new ScreenManager(Display.DEFAULT_DISPLAY);
        screenManager.setBrightnessMode(ScreenManager.BRIGHTNESS_MODE_MANUAL);
        screenManager.setBrightness(24);
    }

    public void createScreensaver() {
        final ScreenManager screenManager = new ScreenManager(Display.DEFAULT_DISPLAY);
        screenManager.setBrightnessMode(ScreenManager.BRIGHTNESS_MODE_MANUAL);
        screenManager.setBrightness(255);
        presenter.createScreensaver();
    }

    public void destroyScreensaver() {
        presenter.destroyScreensaver();
    }

    private void initScreen() {
        final ScreenManager screenManager = new ScreenManager(Display.DEFAULT_DISPLAY);
        screenManager.setDisplayDensity(160);
        screenManager.lockRotation(ScreenManager.ROTATION_180);
//        screenManager.setBrightnessMode(ScreenManager.BRIGHTNESS_MODE_AUTOMATIC);
        screenManager.setBrightnessMode(ScreenManager.BRIGHTNESS_MODE_MANUAL);
        screenManager.setBrightness(255);
        screenManager.setScreenOffTimeout(180L, TimeUnit.SECONDS);
    }

    private void initRealm() {
        Realm.init(this);
        //TODO: Enable encryption
        final RealmConfiguration config = new RealmConfiguration.Builder()
            .name(BuildConfig.DATABASE_NAME)
            .schemaVersion(BuildConfig.DATABASE_VERSION)
//            .deleteRealmIfMigrationNeeded()
            .initialData(realm -> {
                final Settings settings = new Settings();
                settings.id(0L);
                settings.timezone(Constants.DEFAULT_TIMEZONE);
                settings.formatClock(Constants.CLOCK_MODE_24H);
                settings.unitTemperature(Constants.UNIT_TEMPERATURE_CELSIUS);
                settings.unitPressure(Constants.UNIT_PRESSURE_PASCAL);
                settings.formatDate(Constants.DEFAULT_FORMAT_DATE);
                settings.formatTime(Constants.FORMAT_TIME_LONG_24H);
                realm.insert(settings);
            })
            .build();

        Realm.setDefaultConfiguration(config);
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

    private void initPresenter() {
        presenter = new ApplicationPresenter(this);
        presenter.subscribe();
        presenter.createScreensaver();
    }

    private void initSensors() {
        initSensorManager();

        initBME280();
        initTSL2561();
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/WorkSans-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build());
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void initSensorManager() {
        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
                @Override
                public void onDynamicSensorConnected(Sensor sensor) {
                    switch (sensor.getType()) {
                        case Sensor.TYPE_AMBIENT_TEMPERATURE:
                        case Sensor.TYPE_RELATIVE_HUMIDITY:
                        case Sensor.TYPE_PRESSURE:
                        case Sensor.TYPE_LIGHT:
                            sensorManager.registerListener(ThermopileApp.this, sensor, SensorManager.SENSOR_DELAY_UI);
                            break;
                    }
                }
            });
        }
    }

    private void initBME280() {
        try {
//            final BME280SensorDriver bme280SensorDriver = new BME280SensorDriver("I2C1");
//            bme280SensorDriver.registerTemperatureSensor();
//            bme280SensorDriver.registerHumiditySensor();
//            bme280SensorDriver.registerPressureSensor();
            final Bmx280SensorDriver bmx280SensorDriver = new Bmx280SensorDriver("I2C1");
            bmx280SensorDriver.registerTemperatureSensor();
            bmx280SensorDriver.registerHumiditySensor();
            bmx280SensorDriver.registerPressureSensor();
        } catch (IOException e) {
            showError(e);
        }
    }

    private void initTSL2561() {
        try {
            final TSL2561SensorDriver tsl2561SensorDriver = new TSL2561SensorDriver("I2C1");
            tsl2561SensorDriver.registerLuminositySensor();
        } catch (IOException e) {
            showError(e);
        }
    }

    public boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? PackageManager.FEATURE_EMBEDDED : "android.hardware.type.embedded");
    }

    private void onLuminosityData(final float value) {
//        Log.i(TAG, value+"");
    }
}

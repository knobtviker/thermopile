package com.knobtviker.thermopile.drivers;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.google.android.things.device.TimeManager;
import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.android.things.contrib.community.driver.bme280.BME280SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.ds3231.Ds3231;
import com.knobtviker.android.things.contrib.community.driver.ds3231.Ds3231SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.lsm9ds1.Lsm9ds1SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.thermopile.shared.MessageFactory;
import com.knobtviker.thermopile.shared.constants.Action;
import com.knobtviker.thermopile.shared.constants.Keys;

import java.io.IOException;

import timber.log.Timber;

public class DriversService extends Service implements SensorEventListener {

    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_TEMPERATURE = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_PRESSURE = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_HUMIDITY = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_AIR_QUALITY = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_LUMINOSITY = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_ACCELERATION = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_ANGULAR_VELOCITY = 200;
    private int LOW_PASS_FILTER_SMOOTHING_FACTOR_MAGNETIC_FIELD = 200;

    private BME280SensorDriver bme280SensorDriver;
    private Bme680SensorDriver bme680SensorDriver;
    private Ds3231SensorDriver ds3231SensorDriver;
    private TSL2561SensorDriver tsl2561SensorDriver;
    private Lsm9ds1SensorDriver lsm9ds1SensorDriver;

    private SensorManager sensorManager;
    private SensorManager.DynamicSensorCallback sensorCallback;

    @NonNull
    private IncomingHandler incomingHandler;

    @NonNull
    private Messenger serviceMessenger;

    @NonNull
    private static Messenger foregroundMessenger;

    private float currentTemperature = 0.0f;
    private float currentPressure = 0.0f;
    private float currentHumidity = 0.0f;
    private float currentAirQuality = 0.0f;
    private float currentLuminosity = 0.0f;
    private float currentAccelerationVector = 0.0f;
    private float currentAngularVelocityVector = 0.0f;
    private float currentMagneticFieldVector = 0.0f;

    private float smoothedTemperature = 0.0f;
    private float smoothedPressure = 0.0f;
    private float smoothedHumidity = 0.0f;
    private float smoothedAirQuality = 0.0f;
    private float smoothedLuminosity = 0.0f;
    private float smoothedAcceleration = 0.0f;
    private float smoothedAngularVelocity = 0.0f;
    private float smoothedMagneticField = 0.0f;

    private long lastUpdateTemperature = 0L;
    private long lastUpdatePressure = 0L;
    private long lastUpdateHumidity = 0L;
    private long lastUpdateAirQuality = 0L;
    private long lastUpdateLuminosity = 0L;
    private long lastUpdateAcceleration = 0L;
    private long lastUpdateAngularVelocity = 0L;
    private long lastUpdateMagneticField = 0L;

    @Override
    public void onCreate() {
        plantTree();
        prepareLowPassFilters();
        setupMessenger();
        setupSensors();
    }

    @Override
    public void onDestroy() {
        destroySensors();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sendTemperature(event);
                break;
            case Sensor.TYPE_PRESSURE:
                sendPressure(event);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sendHumidity(event);
                break;
            case Sensor.TYPE_LIGHT:
                sendLuminosity(event);
                break;
            case Sensor.TYPE_ACCELEROMETER: //[m/s^2]
                sendAcceleration(event);
                break;
            case Sensor.TYPE_GYROSCOPE: //[°/s]
                sendAngularVelocity(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sendMagneticField(event);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (event.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    sendAirQuality(event);
                    break;
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    private void plantTree() {
        Timber.plant(new Timber.DebugTree());
    }

    private void prepareLowPassFilters() {
        final long now = SystemClock.currentThreadTimeMillis();

        lastUpdateTemperature = now;
        lastUpdatePressure = now;
        lastUpdateHumidity = now;
        lastUpdateAirQuality = now;
        lastUpdateLuminosity = now;
    }

    private void setupMessenger() {
        incomingHandler = new IncomingHandler();
        serviceMessenger = new Messenger(incomingHandler);
    }

    private void setupSensors() {
        try {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensorCallback = new SensorManager.DynamicSensorCallback() {
                @Override
                public void onDynamicSensorConnected(Sensor sensor) {
                    registerSensor(sensor);
                }
            };

            sensorManager.registerDynamicSensorCallback(sensorCallback);

            bme280SensorDriver = bme280();
            bme680SensorDriver = bme680();
            ds3231SensorDriver = ds3231();
            tsl2561SensorDriver = tsl2561();
            lsm9ds1SensorDriver = lsm9ds1();
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void registerSensor(@NonNull final Sensor sensor) {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void destroySensors() {
        sensorManager.unregisterDynamicSensorCallback(sensorCallback);
        sensorManager.unregisterListener(this);

        if (bme280SensorDriver != null) {
            bme280SensorDriver.unregisterTemperatureSensor();
            bme280SensorDriver.unregisterPressureSensor();
            bme280SensorDriver.unregisterHumiditySensor();
            try {
                bme280SensorDriver.close();
            } catch (IOException e) {
                Timber.e(e);
            } finally {
                bme280SensorDriver = null;
            }
        }
        if (bme680SensorDriver != null) {
            bme680SensorDriver.unregisterTemperatureSensor();
            bme680SensorDriver.unregisterPressureSensor();
            bme680SensorDriver.unregisterHumiditySensor();
            bme680SensorDriver.unregisterGasSensor();
            try {
                bme680SensorDriver.close();
            } catch (IOException e) {
                Timber.e(e);
            } finally {
                bme680SensorDriver = null;
            }
        }
        if (ds3231SensorDriver != null) {
            ds3231SensorDriver.unregisterTemperatureSensor();
            try {
                ds3231SensorDriver.close();
            } catch (IOException e) {
                Timber.e(e);
            } finally {
                ds3231SensorDriver = null;
            }
        }
        if (tsl2561SensorDriver != null) {
            tsl2561SensorDriver.unregisterLuminositySensor();
            try {
                tsl2561SensorDriver.close();
            } catch (IOException e) {
                Timber.e(e);
            } finally {
                tsl2561SensorDriver = null;
            }
        }
        if (lsm9ds1SensorDriver != null) {
            lsm9ds1SensorDriver.unregisterTemperatureSensor();
            lsm9ds1SensorDriver.unregisterAccelerometerSensor();
            lsm9ds1SensorDriver.unregisterGyroscopeSensor();
            lsm9ds1SensorDriver.unregisterMagneticFieldSensor();
            try {
                lsm9ds1SensorDriver.close();
            } catch (IOException e) {
                Timber.e(e);
            } finally {
                lsm9ds1SensorDriver = null;
            }
        }
    }

    //CHIP_ID_BME280 = 0x60 | DEFAULT_I2C_ADDRESS = 0x77
    private BME280SensorDriver bme280() throws IOException {
        final BME280SensorDriver bme280SensorDriver = new BME280SensorDriver(BoardDefaults.defaultI2CBus());
        bme280SensorDriver.registerTemperatureSensor();
        bme280SensorDriver.registerPressureSensor();
        bme280SensorDriver.registerHumiditySensor();

        return bme280SensorDriver;
    }

    //CHIP_ID_BME680 = 0x61 | DEFAULT_I2C_ADDRESS = 0x76 (or 0x77)
    private Bme680SensorDriver bme680() throws IOException {
        final Bme680SensorDriver bme680SensorDriver = new Bme680SensorDriver(BoardDefaults.defaultI2CBus());
        bme680SensorDriver.registerTemperatureSensor();
        bme680SensorDriver.registerPressureSensor();
        bme680SensorDriver.registerHumiditySensor();
        bme680SensorDriver.registerGasSensor();
        bme680SensorDriver.setTemperatureOffset(-1);

        return bme680SensorDriver;
    }

    //CHIP_ID_DS3231 = 0x?? | DEFAULT_I2C_ADDRESS = 0x68
    private Ds3231SensorDriver ds3231() throws IOException {
        final Ds3231SensorDriver ds3231SensorDriver = new Ds3231SensorDriver(BoardDefaults.defaultI2CBus());
        ds3231SensorDriver.registerTemperatureSensor();

        final Ds3231 ds3231 = ds3231SensorDriver.device();
        if (ds3231 != null) {
            final long ds3231Timestamp = ds3231.getTimeInMillis();
            final long systemTimeStamp = System.currentTimeMillis();
            // If the DS3231 has a sane timestamp, set the system clock using the DS3231.
            // Otherwise, set the DS3231 using the system time if the system time appears sane
            if (ds3231Timestamp >= Environment.getRootDirectory().lastModified()) {
                //                Log.i(TAG, "Setting system clock using DS3231");
                final TimeManager timeManager = TimeManager.getInstance();
                timeManager.setTime(ds3231Timestamp);

                // Re-enable NTP updates.
                // The call to setTime() disables them automatically, but that's what we use to update our DS3231.
                timeManager.setAutoTimeEnabled(true);
            } else if (systemTimeStamp >= Environment.getRootDirectory().lastModified()) {
                //                Log.i(TAG, "Setting DS3231 time using system clock");
                ds3231.setTime(systemTimeStamp);
            }
        }

        return ds3231SensorDriver;
    }

    //CHIP_ID_TSL2561 = 0x?? | DEFAULT_I2C_ADDRESS = 0x39 (or 0x29 or 0x49)
    private TSL2561SensorDriver tsl2561() throws IOException {
        final TSL2561SensorDriver tsl2561SensorDriver = new TSL2561SensorDriver(BoardDefaults.defaultI2CBus());
        tsl2561SensorDriver.registerLuminositySensor();

        return tsl2561SensorDriver;
    }

    //CHIP_ID_LSM9DS1 = 0x?? | DEFAULT_I2C_ADDRESS_ACCEL_GYRO = 0x6B | DEFAULT_I2C_ADDRESS_MAG = 0x1E
    private Lsm9ds1SensorDriver lsm9ds1() throws IOException {
        final Lsm9ds1SensorDriver lsm9ds1SensorDriver = new Lsm9ds1SensorDriver(BoardDefaults.defaultI2CBus());
        lsm9ds1SensorDriver.registerTemperatureSensor();
        lsm9ds1SensorDriver.registerAccelerometerSensor();
        lsm9ds1SensorDriver.registerGyroscopeSensor();
        lsm9ds1SensorDriver.registerMagneticFieldSensor();

        return lsm9ds1SensorDriver;
    }

    private void sendTemperature(@NonNull final SensorEvent event) {
        final float normalizedValue = lowPassFilterTemperature(normalizedToScale(event.values[0]));

        if (normalizedValue != currentTemperature) {
            currentTemperature = normalizedValue;
            incomingHandler.currentTemperature = currentTemperature;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloat(Keys.TEMPERATURE, currentTemperature);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendPressure(@NonNull final SensorEvent event) {
        final float normalizedValue = lowPassFilterPressure(normalizedToScale(event.values[0]));

        if (normalizedValue != currentPressure) {
            currentPressure = normalizedValue;
            incomingHandler.currentPressure = currentPressure;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloat(Keys.PRESSURE, currentPressure);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendHumidity(@NonNull final SensorEvent event) {
        final float normalizedValue = lowPassFilterHumidity(normalizedToScale(event.values[0]));

        if (normalizedValue != currentHumidity) {
            currentHumidity = normalizedValue;
            incomingHandler.currentHumidity = currentHumidity;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloat(Keys.HUMIDITY, currentHumidity);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendAirQuality(@NonNull final SensorEvent event) {
        final float normalizedValue = lowPassFilterAirQuality(normalizedToScale(event.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]));

        if (normalizedValue != currentAirQuality) {
            currentAirQuality = normalizedValue;
            incomingHandler.currentAirQuality = currentAirQuality;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloat(Keys.AIR_QUALITY, currentAirQuality);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendLuminosity(@NonNull final SensorEvent event) {
        final float normalizedValue = lowPassFilterLuminosity(normalizedToScale(event.values[0]));

        if (normalizedValue != currentLuminosity) {
            currentLuminosity = normalizedValue;
            incomingHandler.currentLuminosity = currentLuminosity;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloat(Keys.LUMINOSITY, currentLuminosity);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendAcceleration(@NonNull final SensorEvent event) {
        final float[] buffer = new float[3];
        buffer[0] = normalizedToScale(event.values[0]);
        buffer[1] = normalizedToScale(event.values[1]);
        buffer[2] = normalizedToScale(event.values[2]);

        final float newVector = lowPassFilterAcceleration(
            normalizedToScale((float) Math.sqrt(Math.pow(buffer[0], 2) + Math.pow(buffer[1], 2) + Math.pow(buffer[2], 2))));
        if (newVector != currentAccelerationVector) {
            currentAccelerationVector = newVector;
            incomingHandler.currentAcceleration = buffer;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloatArray(Keys.ACCELERATION, buffer);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendAngularVelocity(@NonNull final SensorEvent event) {
        final float[] buffer = new float[3];
        buffer[0] = normalizedToScale(event.values[0]);
        buffer[1] = normalizedToScale(event.values[1]);
        buffer[2] = normalizedToScale(event.values[2]);

        final float newVector = lowPassFilterAngularVelocity(
            normalizedToScale((float) Math.sqrt(Math.pow(buffer[0], 2) + Math.pow(buffer[1], 2) + Math.pow(buffer[2], 2))));
        if (newVector != currentAngularVelocityVector) {
            currentAngularVelocityVector = newVector;
            incomingHandler.currentAngularVelocity = buffer;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloatArray(Keys.ANGULAR_VELOCITY, buffer);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    private void sendMagneticField(@NonNull final SensorEvent event) {
        final float[] buffer = new float[3];
        buffer[0] = normalizedToScale(event.values[0]);
        buffer[1] = normalizedToScale(event.values[1]);
        buffer[2] = normalizedToScale(event.values[2]);

        final float newVector = lowPassFilterMagneticField(
            normalizedToScale((float) Math.sqrt(Math.pow(buffer[0], 2) + Math.pow(buffer[1], 2) + Math.pow(buffer[2], 2))));
        if (newVector != currentMagneticFieldVector) {
            currentMagneticFieldVector = newVector;
            incomingHandler.currentMagneticField = buffer;

            final Bundle data = new Bundle();
            data.putString(Keys.VENDOR, event.sensor.getVendor());
            data.putString(Keys.NAME, event.sensor.getName());
            data.putFloatArray(Keys.MAGNETIC_FIELD, buffer);

            MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.drivers(data));
        }
    }

    public static class IncomingHandler extends Handler {

        float currentTemperature = 0.0f;
        float currentPressure = 0.0f;
        float currentHumidity = 0.0f;
        float currentAirQuality = 0.0f;
        float currentLuminosity = 0.0f;
        float[] currentAcceleration = {0.0f, 0.0f, 0.0f};
        float[] currentAngularVelocity = {0.0f, 0.0f, 0.0f};
        float[] currentMagneticField = {0.0f, 0.0f, 0.0f};

        @Override
        public void handleMessage(Message message) {
            final Bundle data = new Bundle();

            switch (message.what) {
                case Action.REGISTER:
                    foregroundMessenger = message.replyTo;

                    data.clear();
                    data.putFloat(Keys.TEMPERATURE, currentTemperature);
                    data.putFloat(Keys.PRESSURE, currentPressure);
                    data.putFloat(Keys.HUMIDITY, currentHumidity);
                    data.putFloat(Keys.AIR_QUALITY, currentAirQuality);
                    data.putFloat(Keys.LUMINOSITY, currentLuminosity);
                    data.putFloatArray(Keys.ACCELERATION, currentAcceleration);
                    data.putFloatArray(Keys.ANGULAR_VELOCITY, currentAngularVelocity);
                    data.putFloatArray(Keys.MAGNETIC_FIELD, currentMagneticField);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.drivers(data));
                    break;
                case Action.CURRENT:
                    data.clear();
                    data.putFloat(Keys.TEMPERATURE, currentTemperature);
                    data.putFloat(Keys.PRESSURE, currentPressure);
                    data.putFloat(Keys.HUMIDITY, currentHumidity);
                    data.putFloat(Keys.AIR_QUALITY, currentAirQuality);
                    data.putFloat(Keys.LUMINOSITY, currentLuminosity);
                    data.putFloatArray(Keys.ACCELERATION, currentAcceleration);
                    data.putFloatArray(Keys.ANGULAR_VELOCITY, currentAngularVelocity);
                    data.putFloatArray(Keys.MAGNETIC_FIELD, currentMagneticField);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.drivers(data));
                default:
                    super.handleMessage(message);
            }
        }
    }

    private float lowPassFilterTemperature(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateTemperature;
        smoothedTemperature = normalizedToScale(
            smoothedTemperature + elapsedTime * (newValue - smoothedTemperature) / LOW_PASS_FILTER_SMOOTHING_FACTOR_TEMPERATURE);
        lastUpdateTemperature = now;
        return normalizedToScale(smoothedTemperature);
    }

    private float lowPassFilterPressure(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdatePressure;
        smoothedPressure =
            normalizedToScale(smoothedPressure + elapsedTime * (newValue - smoothedPressure) / LOW_PASS_FILTER_SMOOTHING_FACTOR_PRESSURE);
        lastUpdatePressure = now;
        return normalizedToScale(smoothedPressure);
    }

    private float lowPassFilterHumidity(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateHumidity;
        smoothedHumidity =
            normalizedToScale(smoothedHumidity + elapsedTime * (newValue - smoothedHumidity) / LOW_PASS_FILTER_SMOOTHING_FACTOR_HUMIDITY);
        lastUpdateHumidity = now;
        return normalizedToScale(smoothedHumidity);
    }

    private float lowPassFilterAirQuality(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateAirQuality;
        smoothedAirQuality = normalizedToScale(
            smoothedAirQuality + elapsedTime * (newValue - smoothedAirQuality) / LOW_PASS_FILTER_SMOOTHING_FACTOR_AIR_QUALITY);
        lastUpdateAirQuality = now;
        return normalizedToScale(smoothedAirQuality);
    }

    private float lowPassFilterLuminosity(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateLuminosity;
        smoothedLuminosity = normalizedToScale(
            smoothedLuminosity + elapsedTime * (newValue - smoothedLuminosity) / LOW_PASS_FILTER_SMOOTHING_FACTOR_LUMINOSITY);
        lastUpdateLuminosity = now;
        return normalizedToScale(smoothedLuminosity);
    }

    private float lowPassFilterAcceleration(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateAcceleration;
        smoothedAcceleration = normalizedToScale(
            smoothedAcceleration + elapsedTime * (newValue - smoothedAcceleration) / LOW_PASS_FILTER_SMOOTHING_FACTOR_ACCELERATION);
        lastUpdateAcceleration = now;
        return normalizedToScale(smoothedAcceleration);
    }

    private float lowPassFilterAngularVelocity(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateAngularVelocity;
        smoothedAngularVelocity = normalizedToScale(smoothedAngularVelocity
            + elapsedTime * (newValue - smoothedAngularVelocity) / LOW_PASS_FILTER_SMOOTHING_FACTOR_ANGULAR_VELOCITY);
        lastUpdateAngularVelocity = now;
        return normalizedToScale(smoothedAngularVelocity);
    }

    private float lowPassFilterMagneticField(final float newValue) {
        final long now = SystemClock.currentThreadTimeMillis();
        final long elapsedTime = now - lastUpdateMagneticField;
        smoothedMagneticField = normalizedToScale(
            smoothedMagneticField + elapsedTime * (newValue - smoothedMagneticField) / LOW_PASS_FILTER_SMOOTHING_FACTOR_MAGNETIC_FIELD);
        lastUpdateMagneticField = now;
        return normalizedToScale(smoothedMagneticField);
    }

    private float normalizedToScale(final float input) {
        return Math.round(input * 10) / 10.0f;
    }
}

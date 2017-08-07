package com.knobtviker.thermopile.data.sources.raw.implementation.drivers;

import android.hardware.Sensor;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by bojan on 10/07/2017.
 */

public class TSL2561SensorDriver implements AutoCloseable {
    private static final String TAG = TSL2561SensorDriver.class.getSimpleName();

    private TSL2561 mDevice;

    // DRIVER parameters
    // documented at https://source.android.com/devices/sensors/hal-interface.html#sensor_t
    private static final String DRIVER_VENDOR = "TAOS";
    private static final String DRIVER_NAME = "TSL2561";
    private static final int DRIVER_MIN_DELAY_US = Math.round(1000000.f / TSL2561.MAX_FREQ_HZ);
    private static final int DRIVER_MAX_DELAY_US = Math.round(1000000.f / TSL2561.MIN_FREQ_HZ);

    private LuminosityUserDriver mLuminosityUserDriver;

    /**
     * Create a new framework sensor driver connected on the given bus.
     * The driver emits {@link Sensor} with luminosity data when
     * registered.
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public TSL2561SensorDriver(String bus) throws IOException {
        mDevice = new TSL2561(bus);
    }

    /**
     * Create a new framework sensor driver connected on the given bus and address.
     * The driver emits {@link Sensor} with luminosity data when
     * registered.
     * @param bus I2C bus the sensor is connected to.
     * @param address I2C address of the sensor.
     * @throws IOException
     */
    public TSL2561SensorDriver(String bus, int address) throws IOException {
        mDevice = new TSL2561(bus, address);
    }

    /**
     * Close the driver and the underlying device.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        unregisterLuminositySensor();
        if (mDevice != null) {
            try {
                mDevice.close();
            } catch (Exception ignored) {
            } finally {
                mDevice = null;
            }
        }
    }

    /**
     * Register a {@link UserSensor} that pipes luminosity readings into the Android SensorManager.
     * @see #unregisterLuminositySensor()
     */
    public void registerLuminositySensor() {
        if (mDevice == null) {
            throw new IllegalStateException("cannot register closed driver");
        }

        if (mLuminosityUserDriver == null) {
            mLuminosityUserDriver = new LuminosityUserDriver();
            UserDriverManager.getManager().registerSensor(mLuminosityUserDriver.getUserSensor());
        }
    }

    /**
     * Unregister the luminosity {@link UserSensor}.
     */
    public void unregisterLuminositySensor() {
        if (mLuminosityUserDriver != null) {
            UserDriverManager.getManager().unregisterSensor(mLuminosityUserDriver.getUserSensor());
            mLuminosityUserDriver = null;
        }
    }

    private class LuminosityUserDriver extends UserSensorDriver {
        // DRIVER parameters
        // documented at https://source.android.com/devices/sensors/hal-interface.html#sensor_t
        private static final float DRIVER_MAX_RANGE = TSL2561.MAX_LUMINOSITY_LUX;
        private static final float DRIVER_RESOLUTION = 0.002f; //0.002f for max sensitivity and 0.9f for lowest sensitivity
        private static final float DRIVER_POWER = TSL2561.MAX_POWER_CONSUMPTION_UA / 1000.f;
        private static final int DRIVER_VERSION = 1;
        private static final String DRIVER_REQUIRED_PERMISSION = "";

        private boolean mEnabled;
        private UserSensor mUserSensor;

        private UserSensor getUserSensor() {
            if (mUserSensor == null) {
                mUserSensor = new UserSensor.Builder()
                    .setType(Sensor.TYPE_LIGHT)
                    .setName(DRIVER_NAME)
                    .setVendor(DRIVER_VENDOR)
                    .setVersion(DRIVER_VERSION)
                    .setMaxRange(DRIVER_MAX_RANGE)
                    .setResolution(DRIVER_RESOLUTION)
                    .setPower(DRIVER_POWER)
                    .setMinDelay(DRIVER_MIN_DELAY_US)
                    .setRequiredPermission(DRIVER_REQUIRED_PERMISSION)
                    .setMaxDelay(DRIVER_MAX_DELAY_US)
                    .setUuid(UUID.randomUUID())
                    .setDriver(this)
                    .build();
            }
            return mUserSensor;
        }

        @Override
        public UserSensorReading read() throws IOException {
            return new UserSensorReading(new float[]{mDevice.readLuminosity()});
        }

        @Override
        public void setEnabled(boolean enabled) throws IOException {
            mEnabled = enabled;
            mDevice.turnOn();
            mDevice.setGain();
            mDevice.setIntegration();
        }

        private boolean isEnabled() {
            return mEnabled;
        }
    }
}

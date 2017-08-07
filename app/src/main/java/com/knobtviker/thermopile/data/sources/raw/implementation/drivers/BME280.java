package com.knobtviker.thermopile.data.sources.raw.implementation.drivers;

/**
 * Created by bojan on 10/07/2017.
 */

import android.support.annotation.IntDef;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Driver for the BMP/BME 280 temperature sensor.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BME280 implements AutoCloseable {
    private static final String TAG = BME280.class.getSimpleName();

    /**
     * Chip ID for the BME280
     */
    public static final int CHIP_ID_BME280 = 0x60;
    /**
     * Default I2C address for the sensor.
     */
    public static final int DEFAULT_I2C_ADDRESS = 0x77;

    @Deprecated
    public static final int I2C_ADDRESS = DEFAULT_I2C_ADDRESS;

    // Sensor constants from the datasheet.
    /**
     * Mininum temperature in Celsius the sensor can measure.
     */
    public static final float MIN_TEMP_C = -40f;
    /**
     * Maximum temperature in Celsius the sensor can measure.
     */
    public static final float MAX_TEMP_C = 85f;
    /**
     * Minimum pressure in hPa the sensor can measure.
     */
    public static final float MIN_PRESSURE_HPA = 300f;
    /**
     * Maximum pressure in hPa the sensor can measure.
     */
    public static final float MAX_PRESSURE_HPA = 1100f;
    /**
     * Minimum humidity in percentage the sensor can measure.
     */
    public static final float MIN_HUMIDITY_PERCENT = 0f;
    /**
     * Maximum humidity in percentage the sensor can measure.
     */
    public static final float MAX_HUMIDITY_PERCENT = 100f;
    /**
     * Maximum power consumption in micro-amperes when measuring temperature.
     */
    public static final float MAX_POWER_CONSUMPTION_TEMP_UA = 325f;
    /**
     * Maximum power consumption in micro-amperes when measuring pressure.
     */
    public static final float MAX_POWER_CONSUMPTION_PRESSURE_UA = 720f;
    /**
     * Maximum power consumption in micro-amperes when measuring pressure.
     */
    public static final float MAX_POWER_CONSUMPTION_HUMIDITY_UA = 340f;
    /**
     * Maximum frequency of the measurements.
     */
    public static final float MAX_FREQ_HZ = 181f;
    /**
     * Minimum frequency of the measurements.
     */
    public static final float MIN_FREQ_HZ = 23.1f;

    /**
     * Power mode.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_SLEEP, MODE_FORCED, MODE_NORMAL})
    public @interface Mode {}
    public static final int MODE_SLEEP = 0;
    public static final int MODE_FORCED = 1;
    public static final int MODE_NORMAL = 2;

    /**
     * Oversampling multiplier.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OVERSAMPLING_SKIPPED, OVERSAMPLING_1X, OVERSAMPLING_2X, OVERSAMPLING_4X, OVERSAMPLING_8X, OVERSAMPLING_16X})
    public @interface Oversampling {}
    public static final int OVERSAMPLING_SKIPPED = 0;
    public static final int OVERSAMPLING_1X = 1;
    public static final int OVERSAMPLING_2X = 2;
    public static final int OVERSAMPLING_4X = 3;
    public static final int OVERSAMPLING_8X = 4;
    public static final int OVERSAMPLING_16X = 5;

    // Registers
    private static final int BME280_REG_TEMP_CALIB_1 = 0x88;
    private static final int BME280_REG_TEMP_CALIB_2 = 0x8A;
    private static final int BME280_REG_TEMP_CALIB_3 = 0x8C;

    private static final int BME280_REG_PRESS_CALIB_1 = 0x8E;
    private static final int BME280_REG_PRESS_CALIB_2 = 0x90;
    private static final int BME280_REG_PRESS_CALIB_3 = 0x92;
    private static final int BME280_REG_PRESS_CALIB_4 = 0x94;
    private static final int BME280_REG_PRESS_CALIB_5 = 0x96;
    private static final int BME280_REG_PRESS_CALIB_6 = 0x98;
    private static final int BME280_REG_PRESS_CALIB_7 = 0x9A;
    private static final int BME280_REG_PRESS_CALIB_8 = 0x9C;
    private static final int BME280_REG_PRESS_CALIB_9 = 0x9E;

    private static final int BME280_REG_HUM_CALIB_1 = 0xA1;
    private static final int BME280_REG_HUM_CALIB_2 = 0xE1;
    private static final int BME280_REG_HUM_CALIB_3 = 0xE3;
    private static final int BME280_REG_HUM_CALIB_4 = 0xE4;
    private static final int BME280_REG_HUM_CALIB_5 = 0xE5;
    private static final int BME280_REG_HUM_CALIB_6 = 0xE6;
    private static final int BME280_REG_HUM_CALIB_7 = 0xE7;

    private static final int BME280_REG_ID = 0xD0;
    private static final int BME280_REG_VERSION = 0xD1;
    private static final int BME280_REG_SOFTRESET = 0xE0;
    private static final int BME280_REG_CTRL = 0xF4;
    private static final int BME280_REG_CTRL_HUM = 0xF2;
    private static final int BME280_REG_CONFIG = 0xF5;

    private static final int BME280_REG_PRESS = 0xF7;
    private static final int BME280_REG_TEMP = 0xFA;
    private static final int BME280_REG_HUM = 0xFD;

    private static final int BME280_POWER_MODE_MASK = 0b00000011;
    private static final int BME280_POWER_MODE_SLEEP = 0b00000000;
    private static final int BME280_POWER_MODE_NORMAL = 0b00000011;

    private static final int BME280_OVERSAMPLING_TEMPERATURE_MASK = 0b11100000;
    private static final int BME280_OVERSAMPLING_TEMPERATURE_BITSHIFT = 5;
    private static final int BME280_OVERSAMPLING_PRESSURE_MASK = 0b00011100;
    private static final int BME280_OVERSAMPLING_PRESSURE_BITSHIFT = 2;
    private static final int BME280_OVERSAMPLING_HUMIDITY_MASK = 0b00000111;
    private static final int BME280_OVERSAMPLING_HUMIDITY_BITSHIFT = 2;

    private I2cDevice mDevice;
    private final int[] mTempCalibrationData = new int[3];
    private final int[] mPressureCalibrationData = new int[9];
    private final int[] mHumidityCalibrationData = new int[6];
    private final byte[] mBuffer = new byte[3]; // for reading temperature and pressure sensor values
    private final byte[] mBufferHumidity = new byte[2]; // for reading temperature and pressure sensor values
    private boolean mEnabled = false;
    private int mChipId;
    private int mMode;
    private int mPressureOversampling;
    private int mTemperatureOversampling;
    private int mHumidityOversampling;

    /**
     * Create a new BMP/BME280 sensor driver connected on the given bus.
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public BME280(String bus) throws IOException {
        this(bus, DEFAULT_I2C_ADDRESS);
    }

    /**
     * Create a new BME280 sensor driver connected on the given bus and address.
     * @param bus I2C bus the sensor is connected to.
     * @param address I2C address of the sensor.
     * @throws IOException
     */
    public BME280(String bus, int address) throws IOException {
        final PeripheralManagerService pioService = new PeripheralManagerService();
        final I2cDevice device = pioService.openI2cDevice(bus, address);
        try {
            connect(device);
        } catch (IOException|RuntimeException e) {
            try {
                close();
            } catch (IOException|RuntimeException ignored) {
            }
            throw e;
        }
    }

    /**
     * Create a new BME280 sensor driver connected to the given I2c device.
     * @param device I2C device of the sensor.
     * @throws IOException
     */
    /*package*/  BME280(I2cDevice device) throws IOException {
        connect(device);
    }

    /**
     * Close the driver and the underlying device.
     */
    @Override
    public void close() throws IOException {
        if (mDevice != null) {
            try {
                mDevice.close();
            } finally {
                mDevice = null;
            }
        }
    }

    private void connect(I2cDevice device) throws IOException {
        mDevice = device;

        mChipId = mDevice.readRegByte(BME280_REG_ID);

        // Read temperature calibration data (3 words). First value is unsigned.
        mTempCalibrationData[0] = mDevice.readRegWord(BME280_REG_TEMP_CALIB_1) & 0xffff;
        mTempCalibrationData[1] = (short) mDevice.readRegWord(BME280_REG_TEMP_CALIB_2);
        mTempCalibrationData[2] = (short) mDevice.readRegWord(BME280_REG_TEMP_CALIB_3);
        // Read pressure calibration data (9 words). First value is unsigned.
        mPressureCalibrationData[0] = mDevice.readRegWord(BME280_REG_PRESS_CALIB_1) & 0xffff;
        mPressureCalibrationData[1] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_2);
        mPressureCalibrationData[2] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_3);
        mPressureCalibrationData[3] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_4);
        mPressureCalibrationData[4] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_5);
        mPressureCalibrationData[5] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_6);
        mPressureCalibrationData[6] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_7);
        mPressureCalibrationData[7] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_8);
        mPressureCalibrationData[8] = (short) mDevice.readRegWord(BME280_REG_PRESS_CALIB_9);
        // Read humidity calibration data (6 words). First value is unsigned.
        mHumidityCalibrationData[0] = mDevice.readRegByte(BME280_REG_HUM_CALIB_1) & 0xFF;
        mHumidityCalibrationData[1] = mDevice.readRegWord(BME280_REG_HUM_CALIB_2);
        mHumidityCalibrationData[2] = mDevice.readRegByte(BME280_REG_HUM_CALIB_3) & 0xFF;
        mHumidityCalibrationData[3] = ((mDevice.readRegByte(BME280_REG_HUM_CALIB_4) & 0xFF) << 4) | ((mDevice.readRegByte(BME280_REG_HUM_CALIB_5) & 0xFF) & 0x0F);
        mHumidityCalibrationData[4] = (((mDevice.readRegByte(BME280_REG_HUM_CALIB_5) & 0xFF) & 0xF0) << 4) | (mDevice.readRegByte(BME280_REG_HUM_CALIB_6) & 0xFF);
        mHumidityCalibrationData[5] = mDevice.readRegByte(BME280_REG_HUM_CALIB_7);
    }

    /**
     * Set the power mode of the sensor.
     * @param mode power mode.
     * @throws IOException
     */
    public void setMode(@Mode int mode) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int regCtrl = mDevice.readRegByte(BME280_REG_CTRL) & 0xff;
        if (mode == MODE_SLEEP) {
            regCtrl &= ~BME280_POWER_MODE_MASK;
        } else {
            regCtrl |= BME280_POWER_MODE_NORMAL;
        }
        mDevice.writeRegByte(BME280_REG_CTRL, (byte)(regCtrl));
        mMode = mode;
    }

    /**
     * Set oversampling multiplier for the temperature measurement.
     * @param oversampling temperature oversampling multiplier.
     * @throws IOException
     */
    public void setTemperatureOversampling(@Oversampling int oversampling) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int regCtrl = mDevice.readRegByte(BME280_REG_CTRL) & 0xff;
        if (oversampling == OVERSAMPLING_SKIPPED) {
            regCtrl &= ~BME280_OVERSAMPLING_TEMPERATURE_MASK;
        } else {
            regCtrl |= 1 << BME280_OVERSAMPLING_TEMPERATURE_BITSHIFT;
        }
        mDevice.writeRegByte(BME280_REG_CTRL, (byte)(regCtrl));
        mTemperatureOversampling = oversampling;
    }

    /**
     * Set oversampling multiplier for the pressure measurement.
     * @param oversampling pressure oversampling multiplier.
     * @throws IOException
     */
    public void setPressureOversampling(@Oversampling int oversampling) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int regCtrl = mDevice.readRegByte(BME280_REG_CTRL) & 0xff;
        if (oversampling == OVERSAMPLING_SKIPPED) {
            regCtrl &= ~BME280_OVERSAMPLING_PRESSURE_MASK;
        } else {
            regCtrl |= 1 << BME280_OVERSAMPLING_PRESSURE_BITSHIFT;
        }
        mDevice.writeRegByte(BME280_REG_CTRL, (byte)(regCtrl));
        mPressureOversampling = oversampling;
    }

    /**
     * Set oversampling multiplier for the pressure measurement.
     * @param oversampling pressure oversampling multiplier.
     * @throws IOException
     */
    public void setHumidityOversampling(@Oversampling int oversampling) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int regCtrl = mDevice.readRegByte(BME280_REG_CTRL_HUM) & 0xff;
        if (oversampling == OVERSAMPLING_SKIPPED) {
            regCtrl &= ~BME280_OVERSAMPLING_HUMIDITY_MASK;
        } else {
            regCtrl |= 1 << BME280_OVERSAMPLING_HUMIDITY_BITSHIFT;
        }
        mDevice.writeRegByte(BME280_REG_CTRL_HUM, (byte)(regCtrl));
        mHumidityOversampling = oversampling;
    }

    /**
     * Returns the sensor chip ID.
     */
    public int getChipId() {
        return mChipId;
    }

    /**
     * Read the current temperature.
     *
     * @return the current temperature in degrees Celsius
     */
    public float readTemperature() throws IOException, IllegalStateException {
        if (mTemperatureOversampling == OVERSAMPLING_SKIPPED) {
            throw new IllegalStateException("BME280 temperature oversampling is skipped");
        }
        final int rawTemp = readSample(BME280_REG_TEMP);
        return compensateTemperature(rawTemp, mTempCalibrationData)[0];
    }

    /**
     * Read the current barometric pressure. If you also intend to use temperature readings, prefer
     * {@link #readTemperatureAndPressure()} instead since sampling the current pressure already
     * requires sampling the current temperature.
     *
     * @return the barometric pressure in hPa units
     * @throws IOException
     */
    public float readPressure() throws IOException, IllegalStateException {
        final float[] values = readTemperatureAndPressure();
        return values[1];
    }

    /**
     * Read the current temperature and barometric pressure.
     *
     * @return a 2-element array. The first element is temperature in degrees Celsius, and the
     * second is barometric pressure in hPa units.
     * @throws IOException
     */
    public float[] readTemperatureAndPressure() throws IOException, IllegalStateException {
        if (mTemperatureOversampling == OVERSAMPLING_SKIPPED) {
            throw new IllegalStateException("BME280 temperature oversampling is skipped.");
        }
        if (mPressureOversampling == OVERSAMPLING_SKIPPED) {
            throw new IllegalStateException("BME280 pressure oversampling is skipped.");
        }
        // The pressure compensation formula requires the fine temperature reading, so we always read temperature first.
        final int rawTemp = readSample(BME280_REG_TEMP);
        final float[] temperatures = compensateTemperature(rawTemp, mTempCalibrationData);
        final int rawPressure = readSample(BME280_REG_PRESS);
        final float pressure = compensatePressure(rawPressure, temperatures[1], mPressureCalibrationData);
        return new float[]{temperatures[0], pressure};
    }

    /**
     * Read the current temperature, humidity and barometric pressure.
     *
     * @return a 3-element array. The first element is temperature in degrees Celsius, second is humidity percentage and the
     * third is barometric pressure in hPa units.
     * @throws IOException
     */
    public float[] readAll() throws IOException, IllegalStateException {
        if (mTemperatureOversampling == OVERSAMPLING_SKIPPED) {
            throw new IllegalStateException("BME280 temperature oversampling is skipped.");
        }
        if (mPressureOversampling == OVERSAMPLING_SKIPPED) {
            throw new IllegalStateException("BME280 pressure oversampling is skipped.");
        }
        if (mHumidityOversampling == OVERSAMPLING_SKIPPED) {
            throw new IllegalStateException("BME280 humidity oversampling is skipped.");
        }
        // The humidity and pressure compensation formula requires the fine temperature reading, so we always read temperature first.
        final int rawTemp = readSample(BME280_REG_TEMP);
        final float[] temperatures = compensateTemperature(rawTemp, mTempCalibrationData);
        final int rawHumidity = readSampleHumidity(BME280_REG_HUM);
        final float humidity = compensateHumidity(rawHumidity, temperatures[1], mHumidityCalibrationData);
        final int rawPressure = readSample(BME280_REG_PRESS);
        final float pressure = compensatePressure(rawPressure, temperatures[1], mPressureCalibrationData);

        return new float[]{temperatures[0], humidity, pressure};
    }

    /**
     * Read the current humidity.
     *
     * @return the current humidity in percentage
     */
    private float readHumidity() throws IOException, IllegalStateException {
        final int rawHumidity = readSampleHumidity(BME280_REG_HUM);
        return compensateHumidity(rawHumidity, readTemperature(), mHumidityCalibrationData);
    }

    /**
     * Reads 20 bits from the given address.
     * @throws IOException
     */
    private int readSample(final int address) throws IOException, IllegalStateException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        synchronized (mBuffer) {
            mDevice.readRegBuffer(address, mBuffer, 3);
            // msb[7:0] lsb[7:0] xlsb[7:4]
            final int msb = mBuffer[0] & 0xff;
            final int lsb = mBuffer[1] & 0xff;
            final int xlsb = mBuffer[2] & 0xf0;
            // Convert to 20bit integer
            return (msb << 16 | lsb << 8 | xlsb) >> 4;
        }
    }

    /**
     * Reads bits from the given address.
     * @throws IOException
     */
    private int readSampleHumidity(final int address) throws IOException, IllegalStateException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        synchronized (mBufferHumidity) {
            mDevice.readRegBuffer(address, mBufferHumidity, 2);
            // msb[7:0] lsb[7:0]
            final int msb = mBuffer[0] & 0xff;
            final int lsb = mBuffer[1] & 0xff;
            // Convert to integer
            return (msb << 8) | lsb;
        }
    }

    // Compensation formula from the BME280 datasheet.
    private float[] compensateTemperature(final int rawTemp, final int[] calibrationData) {
        final int dig_T1 = calibrationData[0];
        final int dig_T2 = calibrationData[1];
        final int dig_T3 = calibrationData[2];

        final float adc_T = (float) rawTemp;
        final float var1 = (adc_T / 16384f - ((float) dig_T1) / 1024f) * ((float) dig_T2);
        final float var2 = ((adc_T / 131072f - ((float) dig_T1) / 8192f) * (adc_T / 131072f - ((float) dig_T1) / 8192f)) * ((float) dig_T3);
        final float fineTemp = var1 + var2;
        return new float[]{fineTemp / 5120.0f, fineTemp};
    }

    // Compensation formula from the BME280 datasheet.
    private float compensatePressure(final int rawPressure, final float fineTemperature, final int[] calibration) {
        final int dig_P1 = calibration[0];
        final int dig_P2 = calibration[1];
        final int dig_P3 = calibration[2];
        final int dig_P4 = calibration[3];
        final int dig_P5 = calibration[4];
        final int dig_P6 = calibration[5];
        final int dig_P7 = calibration[6];
        final int dig_P8 = calibration[7];
        final int dig_P9 = calibration[8];

        float var1 = (fineTemperature / 2.0f) - 64000.0f;
        float var2 = var1 * var1 * ((float) dig_P6) / 32768.0f;
        var2 = var2 + var1 * ((float) dig_P5) * 2.0f;
        var2 = (var2 / 4.0f) + (((float) dig_P4) * 65536.0f);
        var1 = (((float) dig_P3) * var1 * var1 / 524288.0f + ((float) dig_P2) * var1) / 524288.0f;
        var1 = (1.0f + var1 / 32768.0f) * ((float) dig_P1);
        if (var1 == 0.0f) {
            return 0.0f; // avoid exception caused by division by zero
        }
        float p = 1048576.0f - (float) rawPressure;
        p = (p - (var2 / 4096.0f)) * 6250.0f / var1;
        var1 = ((float) dig_P9) * p * p / 2147483648.0f;
        var2 = p * ((float) dig_P8) / 32768.0f;
        p = p + (var1 + var2 + ((float) dig_P7)) / 16.0f;
        // p is in Pa, convert to hPa
        return p / 100.0f;
    }

    // Compensation formula from the BME280 datasheet.
    private float compensateHumidity(final int rawHumidity, final float fineTemperature, final int[] calibration) {
        final int dig_H1 = calibration[0];
        final int dig_H2 = calibration[1];
        final int dig_H3 = calibration[2];
        final int dig_H4 = calibration[3];
        final int dig_H5 = calibration[4];
        final int dig_H6 = calibration[5];

        float h = fineTemperature - 76800.0f;
        h = (rawHumidity - (dig_H4 * 64.0f + dig_H5 / 16384.8f * h)) * (dig_H2 / 65536.0f * (1.0f + dig_H6 / 67108864.0f * h * (1.0f + dig_H3 / 67108864.0f * h)));
        h = h * (1.0f - dig_H1 * h / 524288.0f);
        if (h > 100.0f) {
            h = 100.0f;
        } else if (h < 0.0f) {
            h = 0.0f;
        }
        return h;
    }
}

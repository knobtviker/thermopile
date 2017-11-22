package com.knobtviker.thermopile.data.sources.raw.implementation.drivers;

/**
 * Created by bojan on 16/11/2017.
 */

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Driver for the Bosch BME 680 sensor.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BME680 implements AutoCloseable {
    private static final String TAG = BME680.class.getSimpleName();

    /**
     * Chip ID for the BME680
     */
    public static final int CHIP_ID_BME680 = 0x61;
    /**
     * Default I2C address for the sensor.
     */
    public static final int DEFAULT_I2C_ADDRESS = 0x76;

    @Deprecated
    public static final int I2C_ADDRESS = DEFAULT_I2C_ADDRESS;

    /**
     * Power mode.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_SLEEP, MODE_FORCED})
    public @interface Mode {
    }

    public static final int MODE_SLEEP = 0;
    public static final int MODE_FORCED = 1;

    /**
     * Oversampling multiplier.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OVERSAMPLING_SKIPPED, OVERSAMPLING_1X, OVERSAMPLING_2X, OVERSAMPLING_4X, OVERSAMPLING_8X, OVERSAMPLING_16X})
    public @interface Oversampling {
    }

    public static final int OVERSAMPLING_SKIPPED = 0;
    public static final int OVERSAMPLING_1X = 1;
    public static final int OVERSAMPLING_2X = 2;
    public static final int OVERSAMPLING_4X = 3;
    public static final int OVERSAMPLING_8X = 4;
    public static final int OVERSAMPLING_16X = 5;

    /**
     * IIR filter size.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FILTER_SIZE_NONE, FILTER_SIZE_1, FILTER_SIZE_3, FILTER_SIZE_7, FILTER_SIZE_15, FILTER_SIZE_31, FILTER_SIZE_63, FILTER_SIZE_127})
    public @interface Filter {
    }

    public static final int FILTER_SIZE_NONE = 0;
    public static final int FILTER_SIZE_1 = 1;
    public static final int FILTER_SIZE_3 = 2;
    public static final int FILTER_SIZE_7 = 3;
    public static final int FILTER_SIZE_15 = 4;
    public static final int FILTER_SIZE_31 = 5;
    public static final int FILTER_SIZE_63 = 6;
    public static final int FILTER_SIZE_127 = 7;

    // Registers
    private static final int BME680_REG_ID = 0xD0;
    private static final int BME680_REG_SOFT_RESET = 0xe0;

    // Sensor configuration registers
    private static final int BME680_CONF_HEAT_CTRL_ADDR = 0x70;
    private static final int BME680_CONF_ODR_RUN_GAS_NBC_ADDR = 0x71;
    private static final int BME680_CONF_OS_H_ADDR = 0x72;
    private static final int BME680_MEM_PAGE_ADDR = 0xf3;
    private static final int BME680_CONF_T_P_MODE_ADDR = 0x74;
    private static final int BME680_CONF_ODR_FILT_ADDR = 0x75;

    // Commands
    private static final int BME680_CMD_SOFT_RESET = 0xb6;

    // BME680 coefficients related defines
    private static final int COEFF_ADDR1_LEN = 25;
    private static final int COEFF_ADDR2_LEN = 16;

    // Coefficient's address
    private static final int COEFF_ADDR1 = 0x89;
    private static final int COEFF_ADDR2 = 0xe1;

    // Other coefficient's address
    private static final int BME680_ADDR_RES_HEAT_VAL_ADDR = 0x00;
    private static final int BME680_ADDR_RES_HEAT_RANGE_ADDR = 0x02;
    private static final int BME680_ADDR_RANGE_SW_ERR_ADDR = 0x04;
    private static final int BME680_ADDR_SENS_CONF_START = 0x5A;
    private static final int BME680_ADDR_GAS_CONF_START = 0x64;

    // Mask definitions
    private static final int BME680_GAS_MEAS_MSK = 0x30;
    private static final int BME680_NBCONV_MSK = 0X0F;
    private static final int BME680_FILTER_MSK = 0X1C;
    private static final int BME680_OST_MSK = 0XE0;
    private static final int BME680_OSP_MSK = 0X1C;
    private static final int BME680_OSH_MSK = 0X07;
    private static final int BME680_HCTRL_MSK = 0x08;
    private static final int BME680_RUN_GAS_MSK = 0x10;
    private static final int BME680_MODE_MSK = 0x03;
    private static final int BME680_RHRANGE_MSK = 0x30;
    private static final int BME680_RSERROR_MSK = 0xf0;
    private static final int BME680_NEW_DATA_MSK = 0x80;
    private static final int BME680_GAS_INDEX_MSK = 0x0f;
    private static final int BME680_GAS_RANGE_MSK = 0x0f;
    private static final int BME680_GASM_VALID_MSK = 0x20;
    private static final int BME680_HEAT_STAB_MSK = 0x10;
    private static final int BME680_MEM_PAGE_MSK = 0x10;
    private static final int BME680_SPI_RD_MSK = 0x80;
    private static final int BME680_SPI_WR_MSK = 0x7f;
    private static final int BME680_BIT_H1_DATA_MSK = 0x0F;

    // Bit position definitions for sensor settings
    private static final int GAS_MEAS_POS = 4;
    private static final int FILTER_POS = 2;
    private static final int OST_POS = 5;
    private static final int OSP_POS = 2;
    private static final int OSH_POS = 0;
    private static final int RUN_GAS_POS = 4;
    private static final int MODE_POS = 0;
    private static final int NBCONV_POS = 0;

    // Array Index to Field data mapping for Calibration Data
    private static final int BME680_T2_LSB_REG = 1;
    private static final int BME680_T2_MSB_REG = 2;
    private static final int BME680_T3_REG = 3;
    private static final int BME680_P1_LSB_REG = 5;
    private static final int BME680_P1_MSB_REG = 6;
    private static final int BME680_P2_LSB_REG = 7;
    private static final int BME680_P2_MSB_REG = 8;
    private static final int BME680_P3_REG = 9;
    private static final int BME680_P4_LSB_REG = 11;
    private static final int BME680_P4_MSB_REG = 12;
    private static final int BME680_P5_LSB_REG = 13;
    private static final int BME680_P5_MSB_REG = 14;
    private static final int BME680_P7_REG = 15;
    private static final int BME680_P6_REG = 16;
    private static final int BME680_P8_LSB_REG = 19;
    private static final int BME680_P8_MSB_REG = 20;
    private static final int BME680_P9_LSB_REG = 21;
    private static final int BME680_P9_MSB_REG = 22;
    private static final int BME680_P10_REG = 23;
    private static final int BME680_H2_MSB_REG = 25;
    private static final int BME680_H2_LSB_REG = 26;
    private static final int BME680_H1_LSB_REG = 26;
    private static final int BME680_H1_MSB_REG = 27;
    private static final int BME680_H3_REG = 28;
    private static final int BME680_H4_REG = 29;
    private static final int BME680_H5_REG = 30;
    private static final int BME680_H6_REG = 31;
    private static final int BME680_H7_REG = 32;
    private static final int BME680_T1_LSB_REG = 33;
    private static final int BME680_T1_MSB_REG = 34;
    private static final int BME680_GH2_LSB_REG = 35;
    private static final int BME680_GH2_MSB_REG = 36;
    private static final int BME680_GH1_REG = 37;
    private static final int BME680_GH3_REG = 38;

    private static final int BME680_HUM_REG_SHIFT_VAL = 4;
    private static final int BME680_RESET_PERIOD_MILISECONDS = 10;
    private static final int BME680_POLL_PERIOD_MILISECONDS = 10;

    private I2cDevice mDevice;
    private final int[] mTempCalibrationData = new int[3];
    private final int[] mPressureCalibrationData = new int[10];
    private final int[] mHumidityCalibrationData = new int[7];
    private final int[] mGasHeaterCalibrationData = new int[3];
    private int mHeaterResistanceRange;
    private int mHeaterResistanceValue;
    private int mErrorRange;

    private boolean mEnabled = false;
    private int mChipId;
    private int mPowerMode;

    /**
     * Create a new BME680 sensor driver connected on the given bus.
     *
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public BME680(@NonNull final String bus) throws IOException {
        this(bus, DEFAULT_I2C_ADDRESS);
    }

    /**
     * Create a new BME680 sensor driver connected on the given bus and address.
     *
     * @param bus     I2C bus the sensor is connected to.
     * @param address I2C address of the sensor.
     * @throws IOException
     */
    public BME680(@NonNull final String bus, final int address) throws IOException {
        final PeripheralManagerService pioService = new PeripheralManagerService();
        final I2cDevice device = pioService.openI2cDevice(bus, address);
        try {
            connect(device);
        } catch (IOException | RuntimeException e) {
            try {
                close();
            } catch (IOException | RuntimeException ignored) {
            }
            throw e;
        }
    }

    /**
     * Create a new BME680 sensor driver connected to the given I2c device.
     *
     * @param device I2C device of the sensor.
     * @throws IOException
     */
    /*package*/  BME680(I2cDevice device) throws IOException {
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

        mChipId = mDevice.readRegByte(BME680_REG_ID);

        // Initiate a soft reset
        softReset();

        // Set power mode
        setPowerMode(MODE_SLEEP);

        // Read calibration data in 2 parts and concat them into 1 array
        final byte[] mCalibrationArray = calibrate();

        // Read temperature calibration data (3 words)
        mTempCalibrationData[0] = concatBytes(mCalibrationArray[BME680_T1_MSB_REG], mCalibrationArray[BME680_T1_LSB_REG]) & 0xffff;
        mTempCalibrationData[1] = concatBytes(mCalibrationArray[BME680_T2_MSB_REG], mCalibrationArray[BME680_T2_LSB_REG]);
        mTempCalibrationData[2] = (short) mCalibrationArray[BME680_T3_REG];

        // Read pressure calibration data (10 words)
        mPressureCalibrationData[0] = concatBytes(mCalibrationArray[BME680_P1_MSB_REG], mCalibrationArray[BME680_P1_LSB_REG]) & 0xffff;
        mPressureCalibrationData[1] = concatBytes(mCalibrationArray[BME680_P2_MSB_REG], mCalibrationArray[BME680_P2_LSB_REG]);
        mPressureCalibrationData[2] = (short) mCalibrationArray[BME680_P3_REG];
        mPressureCalibrationData[3] = concatBytes(mCalibrationArray[BME680_P4_MSB_REG], mCalibrationArray[BME680_P4_LSB_REG]);
        mPressureCalibrationData[4] = concatBytes(mCalibrationArray[BME680_P5_MSB_REG], mCalibrationArray[BME680_P5_LSB_REG]);
        mPressureCalibrationData[5] = (short) mCalibrationArray[BME680_P6_REG];
        mPressureCalibrationData[6] = (short) mCalibrationArray[BME680_P7_REG];
        mPressureCalibrationData[7] = concatBytes(mCalibrationArray[BME680_P8_MSB_REG], mCalibrationArray[BME680_P8_LSB_REG]);
        mPressureCalibrationData[8] = concatBytes(mCalibrationArray[BME680_P9_MSB_REG], mCalibrationArray[BME680_P9_LSB_REG]);
        mPressureCalibrationData[9] = (short) mCalibrationArray[BME680_P10_REG]; //this is really uint8_t

        // Read humidity calibration data (7 words)
        mHumidityCalibrationData[0] = (((mCalibrationArray[BME680_H1_MSB_REG] & 0xffff) << BME680_HUM_REG_SHIFT_VAL) | (mCalibrationArray[BME680_H1_LSB_REG] & BME680_BIT_H1_DATA_MSK)) & 0xffff;
        mHumidityCalibrationData[1] = (((mCalibrationArray[BME680_H2_MSB_REG] & 0xffff) << BME680_HUM_REG_SHIFT_VAL) | (mCalibrationArray[BME680_H2_LSB_REG] >> BME680_HUM_REG_SHIFT_VAL)) & 0xffff;
        mHumidityCalibrationData[2] = (short) mCalibrationArray[BME680_H3_REG];
        mHumidityCalibrationData[3] = (short) mCalibrationArray[BME680_H4_REG];
        mHumidityCalibrationData[4] = (short) mCalibrationArray[BME680_H5_REG];
        mHumidityCalibrationData[5] = (short) mCalibrationArray[BME680_H6_REG]; //this is really uint8_t
        mHumidityCalibrationData[6] = (short) mCalibrationArray[BME680_H7_REG];

        // Read gas heater calibration data (3 words)
        mGasHeaterCalibrationData[0] = (short) mCalibrationArray[BME680_GH1_REG];
        mGasHeaterCalibrationData[1] = concatBytes(mCalibrationArray[BME680_GH2_MSB_REG], mCalibrationArray[BME680_GH2_LSB_REG]);
        mGasHeaterCalibrationData[2] = (short) mCalibrationArray[BME680_GH3_REG];

        // Read other heater calibration data
        mHeaterResistanceRange = (short) ((mDevice.readRegByte(BME680_ADDR_RES_HEAT_RANGE_ADDR) & BME680_RHRANGE_MSK) / 16);
        mHeaterResistanceValue = (short) mDevice.readRegByte(BME680_ADDR_RES_HEAT_VAL_ADDR);
        mErrorRange = ((short) mDevice.readRegByte(BME680_ADDR_RANGE_SW_ERR_ADDR) & (short) BME680_RSERROR_MSK) / 16;

        /*
        self.set_humidity_oversample(OS_2X)
        self.set_pressure_oversample(OS_4X)
        self.set_temperature_oversample(OS_8X)
        self.set_filter(FILTER_SIZE_3)
        self.set_gas_status(ENABLE_GAS_MEAS)

        self.get_sensor_data()
            */
    }

    private void softReset() throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        mDevice.writeRegByte(BME680_REG_SOFT_RESET, (byte) BME680_CMD_SOFT_RESET);
        try {
            TimeUnit.MILLISECONDS.sleep(BME680_RESET_PERIOD_MILISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // Set power mode
    private void setPowerMode(int mode) throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int regCtrl = mDevice.readRegByte(BME680_CONF_T_P_MODE_ADDR) & 0xff;
        regCtrl &= ~BME680_MODE_MSK;
        regCtrl |= mode << MODE_POS;
        mDevice.writeRegByte(BME680_CONF_T_P_MODE_ADDR, (byte) (regCtrl));

        this.mPowerMode = mode;

        while (getPowerMode() != this.mPowerMode) {
            try {
                TimeUnit.MILLISECONDS.sleep(BME680_POLL_PERIOD_MILISECONDS);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    // Get power mode
    private int getPowerMode() throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        this.mPowerMode = mDevice.readRegByte(BME680_CONF_T_P_MODE_ADDR) & BME680_MODE_MSK;

        return this.mPowerMode;
    }

    private byte[] calibrate() throws IOException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        final byte[] mCalibrationDataPart1 = new byte[COEFF_ADDR1_LEN];
        final byte[] mCalibrationDataPart2 = new byte[COEFF_ADDR2_LEN];
        mDevice.readRegBuffer(COEFF_ADDR1, mCalibrationDataPart1, COEFF_ADDR1_LEN);
        mDevice.readRegBuffer(COEFF_ADDR2, mCalibrationDataPart2, COEFF_ADDR2_LEN);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(mCalibrationDataPart1);
        outputStream.write(mCalibrationDataPart2);

        return outputStream.toByteArray();
    }

    //TODO: Check if can be replaced with mDevice.readRgWord()
    private int concatBytes(final byte msb, final byte lsb) {
        return (msb << 8) | lsb;
    }
}

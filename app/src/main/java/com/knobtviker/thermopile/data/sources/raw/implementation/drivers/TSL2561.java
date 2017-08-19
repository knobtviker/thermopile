package com.knobtviker.thermopile.data.sources.raw.implementation.drivers;

/**
 * Created by bojan on 01/08/2017.
 */

import android.support.annotation.IntDef;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Driver for the TSL2561 luminosity sensor.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class TSL2561 implements AutoCloseable {
    public static final String TAG = TSL2561.class.getSimpleName();

    /**
     * All possible I2C addresses for the sensor.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TSL2561_ADDRESS_LOW, TSL2561_ADDRESS_FLOAT, TSL2561_ADDRESS_HIGH})
    public @interface Address {
    }

    private static final int TSL2561_ADDRESS_LOW = 0x29;
    private static final int TSL2561_ADDRESS_FLOAT = 0x39;
    private static final int TSL2561_ADDRESS_HIGH = 0x49;

    /**
     * Default I2C address for the sensor.
     */
    public static final int DEFAULT_I2C_ADDRESS = TSL2561_ADDRESS_FLOAT;

    @Deprecated
    public static final int I2C_ADDRESS = DEFAULT_I2C_ADDRESS;

    /**
     * Package
     * Lux calculations differ slightly for CS package
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CHIP_SCALE, T_FN_CL})
    public @interface Package {
    }

    private static final int CHIP_SCALE = 0;
    private static final int T_FN_CL = 1;

    /**
     * Channels of light spectrum.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TSL2561_FULL, TSL2561_INFRARED, TSL2561_VISIBLE})
    public @interface Channel {
    }

    private static final int TSL2561_FULL = 0; // channel 0
    private static final int TSL2561_INFRARED = 1; // channel 1
    private static final int TSL2561_VISIBLE = 2; // channel 0 - channel 1

    /**
     * Gain multiplier.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TSL2561_GAIN_1X, TSL2561_GAIN_16X})
    public @interface Gain {
    }

    public static final int TSL2561_GAIN_1X = 0x00;
    public static final int TSL2561_GAIN_16X = 0x10;

    /**
     * Integration time multiplier.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TSL2561_INTEGRATIONTIME_13MS, TSL2561_INTEGRATIONTIME_101MS, TSL2561_INTEGRATIONTIME_402MS})
    public @interface IntegrationTime {
    }

    public static final int TSL2561_INTEGRATIONTIME_13MS = 0x00; // rather 13.7ms
    public static final int TSL2561_INTEGRATIONTIME_101MS = 0x01;
    public static final int TSL2561_INTEGRATIONTIME_402MS = 0x02;

    /**
     * Auto-gain thresholds.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TSL2561_AGC_THI_13MS, TSL2561_AGC_TLO_13MS, TSL2561_AGC_THI_101MS, TSL2561_AGC_TLO_101MS, TSL2561_AGC_THI_402MS, TSL2561_AGC_TLO_402MS})
    public @interface AutoGainThresholds {
    }

    private static final int TSL2561_AGC_THI_13MS = 4850; // Max value at Ti 13ms = 5047
    private static final int TSL2561_AGC_TLO_13MS = 100;
    private static final int TSL2561_AGC_THI_101MS = 36000; // Max value at Ti 101ms = 37177
    private static final int TSL2561_AGC_TLO_101MS = 200;
    private static final int TSL2561_AGC_THI_402MS = 63000; // Max value at Ti 402ms = 65535
    private static final int TSL2561_AGC_TLO_402MS = 500;

    /**
     * Clipping thresholds.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TSL2561_CLIPPING_13MS, TSL2561_CLIPPING_101MS, TSL2561_CLIPPING_402MS})
    public @interface ClippingThresholds {
    }

    /**
     * Maximum power consumption in micro-amperes when measuring luminosity.
     */
    public static final float MAX_POWER_CONSUMPTION_UA = 0.5f;

    /**
     * Maximum luminosity in lux the sensor can measure.
     */
    public static final float MAX_LUMINOSITY_LUX = 40000.0f;

    /**
     * Maximum frequency of the measurements.
     */
    public static final float MAX_FREQ_HZ = 400000.0f;
    /**
     * Minimum frequency of the measurements.
     */
    public static final float MIN_FREQ_HZ = 0.1f;

    private static final int TSL2561_CLIPPING_13MS = 4900;
    private static final int TSL2561_CLIPPING_101MS = 37000;
    private static final int TSL2561_CLIPPING_402MS = 65000;

    //Registers
    private static final int TSL2561_REG_CTRL = 0x00;
    private static final int TSL2561_REG_TIMING = 0x01;
    private static final int TSL2561_REG_THRESHOLD_LOW_L = 0x02;
    private static final int TSL2561_REG_THRESHOLD_HIGH_L = 0x03;
    private static final int TSL2561_REG_THRESHOLD_LOW_H = 0x04;
    private static final int TSL2561_REG_THRESHOLD_HIGH_H = 0x05;
    private static final int TSL2561_REG_INTERRUPT = 0x06;
    private static final int TSL2561_REG_CRC = 0x08;
    private static final int TSL2561_REG_ID = 0x0A;
    private static final int TSL2561_REG_CHANNEL_0_LOW = 0x0C;
    private static final int TSL2561_REG_CHANNEL_0_HIGH = 0x0D;
    private static final int TSL2561_REG_CHANNEL_1_LOW = 0x0E;
    private static final int TSL2561_REG_CHANNEL_1_HIGH = 0x0F;

    private static final int TSL2561_COMMAND_BIT = 0x80;
    private static final int TSL2561_COMMAND_POWERON = 0x03;
    private static final int TSL2561_COMMAND_POWEROFF = 0x00;

    // T, FN and CL package values
    private static final int TSL2561_LUX_K1T = 0x0040; // 0.125 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B1T = 0x01f2; // 0.0304 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M1T = 0x01be; // 0.0272 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K2T = 0x0080; // 0.250 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B2T = 0x0214; // 0.0325 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M2T = 0x02d1; // 0.0440 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K3T = 0x00c0; // 0.375 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B3T = 0x023f; // 0.0351 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M3T = 0x037b; // 0.0544 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K4T = 0x0100; // 0.50 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B4T = 0x0270; // 0.0381 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M4T = 0x03fe; // 0.0624 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K5T = 0x0138; // 0.61 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B5T = 0x016f; // 0.0224 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M5T = 0x01fc; // 0.0310 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K6T = 0x019a; // 0.80 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B6T = 0x00d2; // 0.0128 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M6T = 0x00fb; // 0.0153 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K7T = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B7T = 0x0018; // 0.00146 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M7T = 0x0012; // 0.00112 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K8T = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B8T = 0x0000; // 0.000 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M8T = 0x0000; // 0.000 * 2^LUX_SCALE

    // CS package values
    private static final int TSL2561_LUX_K1C = 0x0043; // 0.130 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B1C = 0x0204; // 0.0315 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M1C = 0x01ad; // 0.0262 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K2C = 0x0085; // 0.260 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B2C = 0x0228; // 0.0337 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M2C = 0x02c1; // 0.0430 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K3C = 0x00c8; // 0.390 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B3C = 0x0253; // 0.0363 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M3C = 0x0363; // 0.0529 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K4C = 0x010a; // 0.520 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B4C = 0x0282; // 0.0392 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M4C = 0x03df; // 0.0605 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K5C = 0x014d; // 0.65 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B5C = 0x0177; // 0.0229 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M5C = 0x01dd; // 0.0291 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K6C = 0x019a; // 0.80 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B6C = 0x0101; // 0.0157 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M6C = 0x0127; // 0.0180 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K7C = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B7C = 0x0037; // 0.00338 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M7C = 0x002b; // 0.00260 * 2^LUX_SCALE
    private static final int TSL2561_LUX_K8C = 0x029a; // 1.3 * 2^RATIO_SCALE
    private static final int TSL2561_LUX_B8C = 0x0000; // 0.000 * 2^LUX_SCALE
    private static final int TSL2561_LUX_M8C = 0x0000; // 0.000 * 2^LUX_SCALE

    private static final int TSL2561_LUX_LUXSCALE = 14; // Scale by 2^14
    private static final int TSL2561_LUX_RATIOSCALE = 9; // Scale ratio by 2^9
    private static final int TSL2561_LUX_CHSCALE = 10; // Scale channel values by 2^10
    private static final int TSL2561_LUX_CHSCALE_TINT0 = 0x7517; // 322/11 * 2^TSL2561_LUX_CHSCALE
    private static final int TSL2561_LUX_CHSCALE_TINT1 = 0x0FE7; // 322/81 * 2^TSL2561_LUX_CHSCALE

    private int sensorPackage = CHIP_SCALE;
    private int gain = TSL2561_GAIN_1X;
    private int integration = TSL2561_INTEGRATIONTIME_402MS;

    private boolean autoGain = true;

    private final byte[] mBuffer = new byte[2]; // for reading luminosity values

    private int ambient = 0;
    private int ir = 0;

    private I2cDevice mDevice;

    public TSL2561(String bus) throws IOException {
        this(bus, DEFAULT_I2C_ADDRESS, CHIP_SCALE);
    }

    public TSL2561(String bus, int address) throws IOException {
        this(bus, address, CHIP_SCALE);
    }

    public TSL2561(String bus, int address, int sensorPackage) throws IOException {
        final PeripheralManagerService pioService = new PeripheralManagerService();
        final I2cDevice device = pioService.openI2cDevice(bus, address);

        this.sensorPackage = sensorPackage;

        try {
            connect(device);
        } catch (IOException | RuntimeException e) {
            try {
                close();
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    /**
     * Create a new TSL2561 sensor driver connected to the given I2c device.
     *
     * @param device I2C device of the sensor.
     * @throws IOException
     */
    /*package*/  TSL2561(I2cDevice device) throws IOException {
        connect(device);
    }

    /**
     * Close the driver and the underlying device.
     */
    @Override
    public void close() throws Exception {
        if (mDevice != null) {
            try {
                turnOff();
                mDevice.close();
            } finally {
                mDevice = null;
            }
        }
    }

    /**
     * Enables or disables trying to automatically improve results by adjusting the gain setting
     */
    public void setAutoGain(final boolean autoGain) {
        this.autoGain = autoGain;
    }

    private void connect(I2cDevice device) throws IOException {
        mDevice = device;

        turnOn();
        setGain();
        setIntegration();
    }

    /**
     * Set gain multiplier to 1x for the measurement.
     *
     * @throws IOException
     */
    public void setGain() throws IOException {
        setGain(TSL2561_GAIN_1X);
    }

    /**
     * Set gain multiplier for the measurement with current integration time.
     *
     * @throws IOException
     */
    public void setGain(int gain) throws IOException {
        setGainAndIntegration(gain, integration);
    }

    /**
     * Set integration time 402ms for the measurement.
     *
     * @throws IOException
     */
    public void setIntegration() throws IOException {
        setIntegration(TSL2561_INTEGRATIONTIME_402MS);
    }

    /**
     * Set integration time for the measurement with current gain.
     *
     * @throws IOException
     */
    public void setIntegration(int integration) throws IOException {
        setGainAndIntegration(gain, integration);
    }

    /**
     * Set gain multiplier for the measurement with integration time.
     *
     * @throws IOException
     */
    public void setGainAndIntegration(int gain, int integration) throws IOException {
        if (gain != TSL2561_GAIN_1X && gain != TSL2561_GAIN_16X) {
            throw new IllegalArgumentException("Bad gain value [" + gain + "]");
        }
        if (integration != TSL2561_INTEGRATIONTIME_13MS && integration != TSL2561_INTEGRATIONTIME_101MS && integration != TSL2561_INTEGRATIONTIME_402MS) {
            throw new IllegalArgumentException("Bad integration time value [" + integration + "]");
        }

        if (gain != this.gain || integration != this.integration) {
            writeSample(TSL2561_REG_TIMING, (byte) (gain | integration));
            this.gain = gain;
            this.integration = integration;

            switch (integration) {
                case TSL2561_INTEGRATIONTIME_13MS:
                    waitFor(14L);
                    break;
                case TSL2561_INTEGRATIONTIME_101MS:
                    waitFor(102L);
                    break;
                case TSL2561_INTEGRATIONTIME_402MS:
                default:
                    waitFor(403L);
                    break;
            }
        }
    }

    /**
     * Device lux range 0.1 - 40,000+
     * see https://learn.adafruit.com/tsl2561/overview
     */
    public float readLuminosity() throws IOException {
        readRawLuminosity();

        // Make sure the sensor isn't saturated! Return 0 lux if the sensor is saturated
        final int clippingThreshold = clippingThreshold(integration);
        if ((ambient > clippingThreshold) || (ir > clippingThreshold)) {
            return 0.0f;
        }

        // Get the correct scale depending on the integration time and gain
        final int channelScale = channelScale(integration, gain);

        // Scale the channel values
        final int channel0 = (ambient * channelScale) >> TSL2561_LUX_CHSCALE;
        final int channel1 = (ir * channelScale) >> TSL2561_LUX_CHSCALE;

        // Find the ratio of the channel values (Channel1/Channel0) with safeguard on Channel0
        final float ratio = channel0 != 0 ? 0.0f : ((channel1 << (TSL2561_LUX_RATIOSCALE + 1)) / (float) channel0);

        //Find package levels
        final int[] levels = packageLevels(ratio);

        return Math.max(0.0f, ((channel0 * levels[0]) - (channel1 * levels[1])));
    }

    /**
     * Reads visible+IR diode from the I2C device
     */
    public int readFull() throws IOException {
        return readSample(TSL2561_REG_CHANNEL_0_LOW);
    }

    /**
     * Reads IR only diode from the I2C device
     */
    public int readIR() throws IOException {
        return readSample(TSL2561_REG_CHANNEL_1_LOW);
    }

    /**
     * Turn on sensor
     *
     * @throws IOException
     */
    public void turnOn() throws IOException {
        writeSample(TSL2561_REG_CTRL, (byte) TSL2561_COMMAND_POWERON);
    }

    /**
     * Turn off sensor
     *
     * @throws IOException
     */
    private void turnOff() throws IOException {
        writeSample(TSL2561_REG_CTRL, (byte) TSL2561_COMMAND_POWEROFF);
    }

    private void readRawLuminosity() throws IOException {
        // If auto gain disabled get a single reading and continue
        if (!autoGain) {
            readData();
            return;
        }

        boolean isValid = false;
        boolean autoGainCheck = false;
        while (!isValid) {
            final int[] thresholds = thresholds(integration);

            readData();

            // Run an auto-gain check if we haven't already done so
            if (!autoGainCheck) {
                if ((ambient < thresholds[0]) && (gain == TSL2561_GAIN_1X)) {
                    // Increase the gain and try again
                    setGain(TSL2561_GAIN_16X);
                    // Drop the previous conversion results
                    readData();
                    // Set a flag to indicate we've adjusted the gain
                    autoGainCheck = true;
                } else if ((ambient > thresholds[1]) && (gain == TSL2561_GAIN_16X)) {
                    // Drop gain to 1x and try again
                    setGain(TSL2561_GAIN_1X);
                    // Drop the previous conversion results
                    readData();
                    // Set a flag to indicate we've adjusted the gain
                    autoGainCheck = true;
                } else {
                    // Nothing to look at here, keep moving ....
                    // Atmosphere is either valid, or we're already at the chips limits
                    isValid = true;
                }
            } else {
                // If we've already adjusted the gain once, just return the new results.
                // This avoids endless loops where a value is at one extreme pre-gain, and the the other extreme post-gain
                isValid = true;
            }
        }
    }

    /**
     * Read luminosity on both channels
     */
    private void readData() throws IOException {
        // Wait x ms for ADC to complete */
        switch (integration) {
            case TSL2561_INTEGRATIONTIME_13MS:
                waitFor(14L);
                break;
            case TSL2561_INTEGRATIONTIME_101MS:
                waitFor(102L);
                break;
            case TSL2561_INTEGRATIONTIME_402MS:
            default:
                waitFor(403L);
                break;
        }

        // Reads a two byte value from channel 0 (visible + infrared)
        ambient = readFull();
        // Reads a two byte value from channel 1 (infrared)
        ir = readIR();
    }

    /**
     * Find low and high thresholds for integration time
     */
    private int[] thresholds(final int integration) {
        switch (integration) {
            case TSL2561_INTEGRATIONTIME_13MS:
                return new int[]{TSL2561_AGC_TLO_13MS, TSL2561_AGC_THI_13MS};
            case TSL2561_INTEGRATIONTIME_101MS:
                return new int[]{TSL2561_AGC_TLO_101MS, TSL2561_AGC_THI_101MS};
            case TSL2561_INTEGRATIONTIME_402MS:
            default:
                return new int[]{TSL2561_AGC_TLO_402MS, TSL2561_AGC_THI_402MS};
        }
    }

    /**
     * Find clipping threshold for integration time
     */
    private int clippingThreshold(final int integration) {
        switch (integration) {
            case TSL2561_INTEGRATIONTIME_13MS:
                return TSL2561_CLIPPING_13MS;
            case TSL2561_INTEGRATIONTIME_101MS:
                return TSL2561_CLIPPING_101MS;
            case TSL2561_INTEGRATIONTIME_402MS:
            default:
                return TSL2561_CLIPPING_402MS;
        }
    }

    /**
     * Find channel scale for integration time and gain
     */
    private int channelScale(final int integration, final int gain) {
        int channelScale = 0;

        switch (integration) {
            case TSL2561_INTEGRATIONTIME_13MS:
                channelScale = TSL2561_LUX_CHSCALE_TINT0;
                break;
            case TSL2561_INTEGRATIONTIME_101MS:
                channelScale = TSL2561_LUX_CHSCALE_TINT1;
                break;
            case TSL2561_INTEGRATIONTIME_402MS:
            default:
                channelScale = (1 << TSL2561_LUX_CHSCALE);
                break;
        }

        // Scale for gain (1x or 16x)
        if (gain == TSL2561_GAIN_1X) {
            channelScale = channelScale << 4;
        }

        return channelScale;
    }

    /**
     * Find levels depending on package
     */
    private int[] packageLevels(float ratio) {
        int b = 0;
        int m = 0;
        switch (sensorPackage) {
            case CHIP_SCALE:
                if ((ratio >= 0) && (ratio <= TSL2561_LUX_K1C)) {
                    b = TSL2561_LUX_B1C;
                    m = TSL2561_LUX_M1C;
                } else if (ratio <= TSL2561_LUX_K2C) {
                    b = TSL2561_LUX_B2C;
                    m = TSL2561_LUX_M2C;
                } else if (ratio <= TSL2561_LUX_K3C) {
                    b = TSL2561_LUX_B3C;
                    m = TSL2561_LUX_M3C;
                } else if (ratio <= TSL2561_LUX_K4C) {
                    b = TSL2561_LUX_B4C;
                    m = TSL2561_LUX_M4C;
                } else if (ratio <= TSL2561_LUX_K5C) {
                    b = TSL2561_LUX_B5C;
                    m = TSL2561_LUX_M5C;
                } else if (ratio <= TSL2561_LUX_K6C) {
                    b = TSL2561_LUX_B6C;
                    m = TSL2561_LUX_M6C;
                } else if (ratio <= TSL2561_LUX_K7C) {
                    b = TSL2561_LUX_B7C;
                    m = TSL2561_LUX_M7C;
                } else if (ratio > TSL2561_LUX_K8C) {
                    b = TSL2561_LUX_B8C;
                    m = TSL2561_LUX_M8C;
                }
                break;
            case T_FN_CL:
                if ((ratio >= 0) && (ratio <= TSL2561_LUX_K1T)) {
                    b = TSL2561_LUX_B1T;
                    m = TSL2561_LUX_M1T;
                } else if (ratio <= TSL2561_LUX_K2T) {
                    b = TSL2561_LUX_B2T;
                    m = TSL2561_LUX_M2T;
                } else if (ratio <= TSL2561_LUX_K3T) {
                    b = TSL2561_LUX_B3T;
                    m = TSL2561_LUX_M3T;
                } else if (ratio <= TSL2561_LUX_K4T) {
                    b = TSL2561_LUX_B4T;
                    m = TSL2561_LUX_M4T;
                } else if (ratio <= TSL2561_LUX_K5T) {
                    b = TSL2561_LUX_B5T;
                    m = TSL2561_LUX_M5T;
                } else if (ratio <= TSL2561_LUX_K6T) {
                    b = TSL2561_LUX_B6T;
                    m = TSL2561_LUX_M6T;
                } else if (ratio <= TSL2561_LUX_K7T) {
                    b = TSL2561_LUX_B7T;
                    m = TSL2561_LUX_M7T;
                } else if (ratio > TSL2561_LUX_K8T) {
                    b = TSL2561_LUX_B8T;
                    m = TSL2561_LUX_M8T;
                }
                break;
        }

        return new int[]{b, m};
    }

    /**
     * Writes bits to the given address.
     *
     * @throws IOException
     */
    private void writeSample(final int address, final byte command) throws IOException, IllegalStateException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        mDevice.writeRegByte(TSL2561_COMMAND_BIT | address, command);
    }

    /**
     * Reads bits from the given address.
     *
     * @throws IOException
     */
    private int readSample(final int address) throws IOException, IllegalStateException {
        if (mDevice == null) {
            throw new IllegalStateException("I2C device not open");
        }

        synchronized (mBuffer) {
            mDevice.readRegBuffer(TSL2561_COMMAND_BIT | address, mBuffer, 2);
            // msb[7:0] lsb[7:0]
            final int lsb = mBuffer[0] & 0xff;
            final int msb = mBuffer[1] & 0xff;
            // Convert to integer
            return (msb << 8) | lsb;
        }
    }

    /**
     * Pause for integration time
     */
    private void waitFor(final long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

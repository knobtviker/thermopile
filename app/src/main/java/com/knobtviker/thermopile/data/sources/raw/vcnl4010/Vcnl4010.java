package com.knobtviker.thermopile.data.sources.raw.vcnl4010;

import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by bojan on 30/01/2018.
 */

@SuppressWarnings("WeakerAccess")
public class Vcnl4010 implements Closeable {
    private static final String TAG = Vcnl4010.class.getSimpleName();

    /**
     * Chip vendor for the VCNL4010
     */
    public static final String CHIP_VENDOR = "Vishay";

    /**
     * Chip name for the VCNL4010
     */
    public static final String CHIP_NAME = "VCNL4010";

    /**
     * Default I2C address for the sensor.
     */
    public static final int DEFAULT_I2C_ADDRESS = 0x13;

    @Deprecated
    public static final int I2C_ADDRESS = DEFAULT_I2C_ADDRESS;

    /**
     * Product ID for the VCNL4010
     */
    public static final int PRODUCT_ID = 0x20;

    /**
     * Revision for the VCNL4010
     */
    public static final int REVISION = 0x01;

    /**
     * LED current. Can be any value from 0 to 20, each number represents 10 mA.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = MIN_CURRENT_STEP, to = MAX_CURRENT_STEP)
    public @interface Current {
    }

    /**
     * Proximity rate selector in measurements/second.
     *
     * If self_timed measurement is running, any new value written in this register will not be taken over until the mode is actualy cycled.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PROXIMITY_RATE_SKIPPED, PROXIMITY_RATE_1_95, PROXIMITY_RATE_3_90625, PROXIMITY_RATE_7_8125, PROXIMITY_RATE_16_625, PROXIMITY_RATE_31_25, PROXIMITY_RATE_62_5, PROXIMITY_RATE_125, PROXIMITY_RATE_250})
    public @interface ProximityRate {
    }

    public static final int PROXIMITY_RATE_SKIPPED = -1;
    public static final int PROXIMITY_RATE_1_95 = 0;
    public static final int PROXIMITY_RATE_3_90625 = 1;
    public static final int PROXIMITY_RATE_7_8125 = 2;
    public static final int PROXIMITY_RATE_16_625 = 3;
    public static final int PROXIMITY_RATE_31_25 = 4;
    public static final int PROXIMITY_RATE_62_5 = 5;
    public static final int PROXIMITY_RATE_125 = 6;
    public static final int PROXIMITY_RATE_250 = 7;

    /**
     * Ambient light continuous conversion mode used for performing faster ambient light measurements.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DISABLE, ENABLE})
    public @interface ContinuousConversion {
    }

    public static final int DISABLE = 0; //default
    public static final int ENABLE = 1;

    /**
     * Ambient light rate selector in samples/second.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AMBIENT_RATE_SKIPPED, AMBIENT_RATE_1, AMBIENT_RATE_2, AMBIENT_RATE_3, AMBIENT_RATE_4, AMBIENT_RATE_5, AMBIENT_RATE_6, AMBIENT_RATE_8, AMBIENT_RATE_10})
    public @interface AmbientRate {
    }

    public static final int AMBIENT_RATE_SKIPPED = -1;
    public static final int AMBIENT_RATE_1 = 0;
    public static final int AMBIENT_RATE_2 = 1; //default
    public static final int AMBIENT_RATE_3 = 2;
    public static final int AMBIENT_RATE_4 = 3;
    public static final int AMBIENT_RATE_5 = 4;
    public static final int AMBIENT_RATE_6 = 5;
    public static final int AMBIENT_RATE_8 = 6;
    public static final int AMBIENT_RATE_10 = 7;

    /**
     * Ambient light continuous conversion mode used for performing faster ambient light measurements.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OFFSET_DISABLE, OFFSET_ENABLE})
    public @interface AutomaticOffset {
    }

    public static final int OFFSET_DISABLE = 0;
    public static final int OFFSET_ENABLE = 1; //default

    /**
     * Averaging function.
     * Sets the number of single conversions done during one measurement cycle.
     * Result is the average value of all conversions.
     *
     * If self_timed measurement is running, any new value written in this register will not be taken over until the mode is actualy cycled.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AVERAGING_SKIPPED, AVERAGING_1, AVERAGING_2, AVERAGING_3, AVERAGING_4, AVERAGING_5, AVERAGING_6, AVERAGING_8, AVERAGING_10})
    public @interface Averaging {
    }

    public static final int AVERAGING_SKIPPED = -1;
    public static final int AVERAGING_1 = 0;
    public static final int AVERAGING_2 = 1;
    public static final int AVERAGING_3 = 2;
    public static final int AVERAGING_4 = 3;
    public static final int AVERAGING_5 = 4;
    public static final int AVERAGING_6 = 5; //default
    public static final int AVERAGING_8 = 6;
    public static final int AVERAGING_10 = 7;

    /**
     * Interrupt type.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({INTERRUPT_TYPE_UNKNOWN, INTERRUPT_TYPE_PROXIMITY, INTERRUPT_TYPE_AMBIENT})
    public @interface InterruptType {
    }

    public static final int INTERRUPT_TYPE_UNKNOWN = -1;
    public static final int INTERRUPT_TYPE_PROXIMITY = 0;
    public static final int INTERRUPT_TYPE_AMBIENT = 1;

    /**
     * Interrupt count.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({INTERUPT_1_COUNT, INTERUPT_2_COUNT, INTERUPT_4_COUNT, INTERUPT_8_COUNT, INTERUPT_16_COUNT, INTERUPT_32_COUNT, INTERUPT_64_COUNT, INTERUPT_128_COUNT})
    public @interface InterruptCount {
    }

    public static final int INTERUPT_1_COUNT = 0;
    public static final int INTERUPT_2_COUNT = 32;
    public static final int INTERUPT_4_COUNT = 64;
    public static final int INTERUPT_8_COUNT = 96;
    public static final int INTERUPT_16_COUNT = 128;
    public static final int INTERUPT_32_COUNT = 160;
    public static final int INTERUPT_64_COUNT = 192;
    public static final int INTERUPT_128_COUNT = 224;

    /**
     * Proximity frequency selector Hz.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PROXIMITY_FREQUENCY_390_625_KHZ, PROXIMITY_FREQUENCY_781_25_KHZ, PROXIMITY_FREQUENCY_1_5625_MHZ, PROXIMITY_FREQUENCY_3_125_MHZ})
    public @interface ProximityFrequency {
    }

    public static final int PROXIMITY_FREQUENCY_390_625_KHZ = 0;
    public static final int PROXIMITY_FREQUENCY_781_25_KHZ = 1;
    public static final int PROXIMITY_FREQUENCY_1_5625_MHZ = 2;
    public static final int PROXIMITY_FREQUENCY_3_125_MHZ = 3;


    // Registers
    /*
    With setting bit 3 and bit 4 at the same write command,
    a simultaneously measurement of ambient light and proximity is done.
    Beside als_en and/or prox_en first selftimed_en needs to be set.
    On-demand measurement modes are disabled if selftimed_en bit is set.
    For the selftimed_en mode changes in reading rates (reg #4 and reg #2)
    can be made only when b0 (selftimed_en bit) = 0.
    For the als_od mode changes to the reg #4 can be made only when b4 (als_od bit) = 0;
    this is to avoid synchronization problems and undefined states between the clock domains.
    In effect this means that it is only reasonable to change rates while no selftimed conversion is ongoing.
     */
    private static final int REG_COMMAND = 0x80;
    private static final int REG_PRODUCT_ID_REVISION = 0x81;
    private static final int REG_PROXIMITY_RATE = 0x82;
    private static final int REG_IR_LED = 0x83;
    private static final int REG_AMBIENT_PARAMETER = 0x84;
    private static final int REG_AMBIENT_DATA = 0x85;
    private static final int REG_PROXIMITY_DATA = 0x87;
    private static final int REG_INTERRUPT_CONTROL = 0x89;
    private static final int REG_LOW_THRESHOLD = 0x8A; //...and 0x8B
    private static final int REG_HIGH_THRESHOLD = 0x8C; //...and 0x8D
    private static final int REG_INTERRUPT_STATUS = 0x8E;
    private static final int REG_MODULATOR_TIMING = 0x8F;

    // Commands
    private static final byte CMD_CLEAR = 0x00;
    private static final byte CMD_INTERRUPT_STOP = 0xF;

    // Masks
    private static final int MASK_INTERRUPT_TYPE_AMBIENT = 0x05;
    private static final int MASK_INTERRUPT_TYPE_PROXIMITY = 0x03;

    // Bit positions
    private static final int POSITION_COMMAND_CONFIG_LOCK = 7;
    private static final int POSITION_COMMAND_AMBIENT_DATA_READY = 6;
    private static final int POSITION_COMMAND_PROXIMITY_DATA_READY = 5;
    private static final int POSITION_COMMAND_AMBIENT_ON_DEMAND = 4;
    private static final int POSITION_COMMAND_PROXIMITY_ON_DEMAND = 3;
    private static final int POSITION_COMMAND_AMBIENT_PERIODIC = 2;
    private static final int POSITION_COMMAND_PROXIMITY_PERIODIC = 1;
    private static final int POSITION_COMMAND_SELF_TIMED = 0;

    // Defines
    private static final int MIN_CURRENT_STEP = 0;
    private static final int MAX_CURRENT_STEP = 20;

    private I2cDevice device;

    /**
     * Create a new VCNL4010 sensor driver connected on the given bus.
     *
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public Vcnl4010(@NonNull final String bus) throws IOException {
        this(bus, DEFAULT_I2C_ADDRESS);
    }

    /**
     * Create a new VCNL4010 sensor driver connected on the given bus and address.
     *
     * @param bus     I2C bus the sensor is connected to.
     * @param address I2C address of the sensor.
     * @throws IOException
     */
    public Vcnl4010(@NonNull final String bus, final int address) throws IOException {
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
     * Create a new VCNL4010 sensor driver connected to the given I2c device.
     *
     * @param device I2C device of the sensor.
     * @throws IOException
     */
    /*package*/  Vcnl4010(I2cDevice device) throws IOException {
        connect(device);
    }

    /**
     * Close the driver and the underlying device.
     */
    @Override
    public void close() throws IOException {
        if (device != null) {
            try {
                device.close();
            } finally {
                device = null;
            }
        }
    }

    //Registre si popisao.
    //Sad za svaki registar prođi kroz bitove i napravi getter ako je read only bit i setter ako ima i write pristup
    //Prođi kroz fusnote o nekim žnj postavkama
    private void connect(I2cDevice device) throws IOException {
        this.device = device;

        final byte productIdRevision = this.device.readRegByte(REG_PRODUCT_ID_REVISION);
        if ((productIdRevision >> 4) != PRODUCT_ID && (productIdRevision & 0x0F) != REVISION) {
            throw new IllegalStateException(String.format("%s %s not found.", CHIP_VENDOR, CHIP_NAME));
        }

        setLEDCurrent(MIN_CURRENT_STEP);
        setProximityRate(PROXIMITY_RATE_SKIPPED);
    }

    /**
     * Read only.
     */
    public boolean getConfigLock() throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        final byte data = device.readRegByte(REG_COMMAND);

        return getBit(data, POSITION_COMMAND_CONFIG_LOCK) == 1;
    }

    /**
     * Set the LED current to any value from 0 to 20, each number represents 10 mA.
     * If you set it to 5, its 50mA. Minimum is 0 (0 mA, off), max is 20 (200mA).
     */
    @SuppressWarnings("SameParameterValue")
    public void setLEDCurrent(@Current final int current10mA) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        byte newValue = (byte) current10mA;

        if (newValue < 0) {
            newValue = 0;
        }
        if (newValue > 20) {
            newValue = 20;
        }

        device.writeRegByte(REG_IR_LED, newValue);
    }

    /**
     * Get the LED current value directly from the register. Each bit represents 10mA so 5 == 50mA.
     */
    @Current
    public int getLEDCurrent() throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        return device.readRegByte(REG_IR_LED);
    }

    /**
     * Set the measurement signal frequency (rate for proximity).
     */
    public void setProximityRate(@ProximityRate final int rate) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }
        if (rate == PROXIMITY_RATE_SKIPPED) {
            return;
        }

        final byte cmd = device.readRegByte(REG_COMMAND);
        device.writeRegByte(REG_COMMAND, (byte) (cmd & 0xFE)); // set selftimed_en to off //TODO: What about method for on/off?
        device.writeRegByte(REG_PROXIMITY_RATE, (byte) rate);
    }

    /**
     * Get proximity measurement. Raw 16-bit reading value, will vary with LED current, unit-less!
     */
    public short readProximity() throws IOException {
        setSelfTimed(false);

        executeMeasurement(POSITION_COMMAND_PROXIMITY_ON_DEMAND);

        return waitForData(POSITION_COMMAND_PROXIMITY_DATA_READY, REG_PROXIMITY_DATA);
    }

    /**
     * Get ambient light measurement. Raw 16-bit reading value, unit-less!
     */
    public short readAmbient() throws IOException {
        setSelfTimed(false);

        executeMeasurement(POSITION_COMMAND_AMBIENT_ON_DEMAND);

        return waitForData(POSITION_COMMAND_AMBIENT_DATA_READY, REG_AMBIENT_DATA);
    }

    /**
     * Get proximity and ambient light measurement.
     */
    public short[] readProximityAndAmbient() throws IOException {
        executeMeasurements(POSITION_COMMAND_AMBIENT_ON_DEMAND, POSITION_COMMAND_PROXIMITY_ON_DEMAND);

        return waitForData(new int[]{POSITION_COMMAND_AMBIENT_DATA_READY, POSITION_COMMAND_PROXIMITY_DATA_READY}, new int[] {REG_AMBIENT_DATA, REG_PROXIMITY_DATA});
    }

    public void setInterrupt(@InterruptType final int type, final short lowThreshold, final short highThreshold) throws IOException {
        setInterrupt(type, INTERUPT_1_COUNT, lowThreshold, highThreshold);
    }

    public void setInterrupt(@InterruptType final int type, @InterruptCount final int count, final short lowThreshold, final short highThreshold) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        final byte INT_THRES_EN = 0x02;
        // Int count exceed | INT_THRES_EN | INT_THRES_SEL
        final int interruptCount = count | INT_THRES_EN | type;

        byte cmd = CMD_CLEAR;
        switch (type) {
            case INTERRUPT_TYPE_PROXIMITY:
                cmd |= MASK_INTERRUPT_TYPE_PROXIMITY;
                break;
            case INTERRUPT_TYPE_AMBIENT:
                cmd |= MASK_INTERRUPT_TYPE_AMBIENT;
                break;
        }

        device.writeRegByte(REG_COMMAND, CMD_CLEAR); // clear cmd register
        device.writeRegWord(REG_LOW_THRESHOLD, lowThreshold); // set low threshold register
        device.writeRegWord(REG_HIGH_THRESHOLD, highThreshold); // set high threshold register
        device.writeRegByte(REG_INTERRUPT_CONTROL, (byte) interruptCount); // set interrupt control register
        device.writeRegByte(REG_COMMAND, cmd); //TODO: Check if this address is REG_INTERRUPT_CONTROL maybe
    }

    public Interrupt getInterrupt() throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        final byte status = device.readRegByte(REG_INTERRUPT_STATUS);

        final Interrupt interrupt = new Interrupt();

        boolean isProximityInterrupt = ((status >> 3) & 1) == 1;
        boolean isAmbientInterrupt = ((status >> 2) & 1) == 1;

        interrupt.type = isProximityInterrupt ? INTERRUPT_TYPE_PROXIMITY : (isAmbientInterrupt ? INTERRUPT_TYPE_AMBIENT : INTERRUPT_TYPE_UNKNOWN);
        interrupt.exceededLowThreshold = ((status >> 1) & 1) == 1;
        interrupt.exceededHighThreshold = (status & 1) == 1;

        return interrupt;
    }

    public void stopInterrupts() throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        device.writeRegByte(REG_COMMAND, CMD_CLEAR); // clear cmd register
    }

    public void clearInterrupt() throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        final byte status = device.readRegByte(REG_INTERRUPT_STATUS); //TODO: WTF is this for?
        device.writeRegByte(REG_INTERRUPT_STATUS, CMD_INTERRUPT_STOP);
    }

    private void applyMask(final int mask) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        byte i = device.readRegByte(REG_INTERRUPT_STATUS);
        i &= ~mask;
        device.writeRegByte(REG_INTERRUPT_STATUS, i);
    }

    private void setSelfTimed(final boolean isSelfTimed) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        byte data = device.readRegByte(REG_COMMAND);

        if (isSelfTimed) {
            data = setBit(data, POSITION_COMMAND_SELF_TIMED);
        } else {
            data = unsetBit(data, POSITION_COMMAND_SELF_TIMED);
        }

        device.writeRegByte(REG_COMMAND, data);
    }

    private void executeMeasurement(final int position) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        byte data = device.readRegByte(REG_COMMAND);
        data = setBit(data, position);

        device.writeRegByte(REG_COMMAND, data);
    }

    private void executeMeasurements(final int... positions) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        byte data = device.readRegByte(REG_COMMAND);
        data = setBit(data, positions[0]);
        data = setBit(data, positions[1]);

        device.writeRegByte(REG_COMMAND, data);
    }

    private short waitForData(final int readyPosition, final int readRegister) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        while (true) {
            final byte data = device.readRegByte(REG_COMMAND);
            if (getBit(data, readyPosition) == 1) {
                return device.readRegWord(readRegister);
            }
            SystemClock.sleep(1);
        }
    }

    private short[] waitForData(final int[] readyPositions, final int[] readRegisters) throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        while (true) {
            final byte data = device.readRegByte(REG_COMMAND);
            if ((getBit(data, readyPositions[0]) == 1) && (getBit(data, readyPositions[1]) == 1)) {
                return new short[]{device.readRegWord(readRegisters[0]), device.readRegWord(readRegisters[1])};
            }
            SystemClock.sleep(1);
        }
    }

    private byte getBit(final byte data, final int position) {
        return (byte) ((data >> position) & 1);
    }

    private byte setBit(final byte data, final int position) {
        return (byte) (data | (1 << position));
    }

    private byte unsetBit(final byte data, final int position) {
        return (byte) (data & ~(1 << position));
    }

    /*
        // Create I2CBus
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        // Get I2C device, VCNL4010 I2C address is 0x13(19)
        I2CDevice device = bus.getDevice(0x13);

        // Select command register
        // Enables ALS and proximity measurement, LP oscillator
        device.write(0x80, (byte)0xFF);
        // Select proximity rate register
        // 1.95 proximity measurement / s
        device.write(0x82, (byte)0x00);
        // Select ALS register
        // Continuos conversion mode, ALS rate 2 samples / s
        device.write(0x84, (byte)0x9D);
        Thread.sleep(800);

        // Read 4 bytes of data
        // luminance msb, luminance lsb, proximity msb, proximity lsb
        byte[] data = new byte[4];
        device.read(0x85, data, 0, 4);

        // Convert the data
        int luminance = ((data[0] & 0xFF) * 256) + (data[1] & 0xFF);
        int proximity = ((data[2] & 0xFF) * 256) + (data[3] & 0xFF);

        // Output data to screen
        System.out.printf("Ambient Light Luminance : %d lux %n", luminance);
        System.out.printf("Proximity Of The Device : %d %n", proximity);
    */

//    byte b = 0xAB;
//    var high = b >> 4;
//    var low = b & 0x0F;

    //To set a bit:
    //
    //my_byte = my_byte | (1 << pos);

    //To un-set a bit:
    //
    //my_byte = my_byte & ~(1 << pos);
}

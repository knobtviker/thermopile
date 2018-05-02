package com.knobtviker.thermopile.data.sources.raw.fram;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.Closeable;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by bojan on 18/01/2018.
 */

public abstract class Fram implements Closeable {

    //These payload sizes are baked into Linux kernel for some reason
    private final int MAX_PAYLOAD_SIZE_WRITE = 8190;
    private final int MAX_PAYLOAD_SIZE_READ = 8192;

    private boolean VERBOSE = false;

    private boolean framInitialised;
    private boolean manualMode;
    private int i2cAddress;
    private int density;
    private int densityCode;
    private String writeProtectPin;
    private boolean writeProtectStatus;
    private int manufacturer;
    private int productId;
    private int maxAddress;

    private Gpio gpioWriteProtect;
    private I2cDevice device;

    // IDs
    //Manufacturers codes
    private final static int FUJITSU_MANUFACT_ID = 0x00A;
    private final static int CYPRESS_MANUFACT_ID = 0x004;
    private final static int MANUALMODE_MANUFACT_ID = 0xF00;
    private final static int MANUALMODE_PRODUCT_ID = 0xF00;
    private final static int MANUALMODE_DENSITY_ID = 0xF00;

    // The density
    public final static int DENSITY_4K = 4;        // 4K
    public final static int DENSITY_16K = 16;        // 4K
    public final static int DENSITY_64K = 64;        // 64K
    public final static int DENSITY_128K = 128;        // 256K
    public final static int DENSITY_256K = 256;        // 256K
    public final static int DENSITY_512K = 512;        // 512K
    public final static int DENSITY_1024K = 1024;        // 1M

    // The density codes gives the memory's adressing scheme
    private final static int DENSITY_CODE_MB85RC04V = 0x00;        // 4K
    private final static int DENSITY_CODE_MB85RC64TA = 0x03;        // 64K
    private final static int DENSITY_CODE_MB85RC256V = 0x05;        // 256K
    private final static int DENSITY_CODE_MB85RC512T = 0x06;        // 512K
    private final static int DENSITY_CODE_MB85RC1MT = 0x07;        // 1M

    private final static int DENSITY_CODE_CY15B128J = 0x01;        // 128K - FM24V01A also
    private final static int DENSITY_CODE_CY15B256J = 0x02;        // 256K - FM24V02A also
    private final static int DENSITY_CODE_FM24V05 = 0x03;        // 512K
    private final static int DENSITY_CODE_FM24V10 = 0x04;        // 1024K

    // Devices:
    // MB85RC16, MB85RC16V, MB85RC64A, MB85RC64V, MB85RC128A and
    // FM24W256,FM24CL64B, FM24C64B, FM24C16B, FM24C04B, FM24CL04B
    // do not support Device ID reading

    private final static int MAXADDRESS_04 = 512;
    private final static int MAXADDRESS_16 = 2048;
    private final static int MAXADDRESS_64 = 8192;
    private final static int MAXADDRESS_128 = 16384;
    private final static int MAXADDRESS_256 = 32768;
    private final static int MAXADDRESS_512 = 65535;
    private final static int MAXADDRESS_1024 = 65535; // 1M devices are in fact managed as 2 512 devices from lib point of view > create 2 instances of the object with each a differnt address

    //Special commands
    private final static int MB85RC_PAGE_BIT = 0x01; //Page select bit (A16), MSB of 17 bit address
    private final static int MASTER_CODE = 0xF8;
    private final static int SLEEP_MODE = 0x86; //Cypress codes, not used here
    private final static int HIGH_SPEED = 0x08; //Cypress codes, not used here

    // Managing Write protect pin
    private final static boolean MANAGE_WRITE_PROTECT = false; //false if WP pin remains not connected
    public final static String DEFAULT_WRITE_PROTECT_PIN = "BCM16"; //write protection pin - active high, write enabled when low
    public final static boolean DEFAULT_WRITE_PROTECT_STATUS = false; //false means protection is off - write is enabled

    /**
     * Create a new MB85RC256V driver connected on the given bus.
     *
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public Fram(@NonNull final String bus, final int address, final boolean writeProtectStatus) throws IOException {
        this(bus, address, DEFAULT_WRITE_PROTECT_PIN, writeProtectStatus, 0, false);
    }

    public Fram(@NonNull final String bus, final int address, @NonNull final String writeProtectPin, final boolean writeProtectStatus) throws IOException {
        this(bus, address, writeProtectPin, writeProtectStatus, 0, false);
    }

    public Fram(@NonNull final String bus, final int address, @NonNull final String writeProtectPin, final boolean writeProtectStatus, final int density) throws IOException {
        this(bus, address, writeProtectPin, writeProtectStatus, density, true);
    }

    /**
     * Create a new MB85RC256V driver connected on the given bus and address.
     *
     * @param bus     I2C bus the sensor is connected to.
     * @param address I2C address of the sensor.
     * @throws IOException
     */
    private Fram(@NonNull final String bus, final int address, @NonNull final String writeProtectPin, final boolean writeProtectStatus, final int density, final boolean manualMode) throws IOException {
        final PeripheralManager peripheralManager = PeripheralManager.getInstance();
        final I2cDevice device = peripheralManager.openI2cDevice(bus, address);

        this.framInitialised = false;

        this.manualMode = manualMode;
        this.i2cAddress = address;
        this.writeProtectPin = writeProtectPin;
        this.writeProtectStatus = writeProtectStatus;
        this.density = density;

        initWriteProtect(writeProtectStatus);

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
     * Create a new MB85RC256V driver connected to the given I2c device.
     *
     * @param device I2C device of the sensor.
     * @throws IOException
     */
    /*package*/  Fram(I2cDevice device) throws IOException {
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

    // Set verbose state
    public void setVerbose(final boolean verbose) {
        VERBOSE = verbose;
    }

    // Returns Write Protect status
    public boolean writeProtectStatus() {
        return writeProtectStatus;
    }

    // Return the readiness of the memory chip
    public boolean initialised() {
        return framInitialised;
    }

    // Enable write protect function of the chip by pulling up WP pin
    public void enableWriteProtect() throws IOException {
        if (MANAGE_WRITE_PROTECT) {
            gpioWriteProtect.setValue(true);
            writeProtectStatus = true;
        } else {
            throw new IOException("Not permitted operation");
        }
    }

    // Disable write protect function of the chip by pulling up WP pin
    public void disableWriteProtect() throws IOException {
        if (MANAGE_WRITE_PROTECT) {
            gpioWriteProtect.setValue(false);
            writeProtectStatus = false;
        } else {
            throw new IOException("Not permitted operation");
        }
    }

    // Reads one bit from the specified FRAM address
    public byte readBit(final int framAddress, final int position) throws IOException {
        if (position > 7) {
            throw new IOException("Bit position out of range");
        } else {
            final byte[] buffer = readArray(framAddress, 1);
            return bitRead(buffer[0], position);
        }
    }

    // Reads one byte from the specified FRAM address
    public byte readByte(final int framAddress) throws IOException {
        final byte[] buffer = readArray(framAddress, 1);

        return buffer[0];
    }

    // Reads a 16bits (word) value from the specified FRAM address
    public short readWord(final int framAddress) throws IOException {
        final byte[] buffer = readArray(framAddress, 2);

        int result = buffer[1] & 0xFF;
        result = (result << 8) | (buffer[0] & 0xFF);

        return (short) result;
    }

    // Read a 32bits value from the specified FRAM address
    public int readInt(final int framAddress) throws IOException {
        final byte[] buffer = readArray(framAddress, 4);

        return buffer[0] << 24 | (buffer[1] & 0xFF) << 16 | (buffer[2] & 0xFF) << 8 | (buffer[3] & 0xFF);
    }

    // Reads an array of bytes from the specified FRAM address
    public byte[] readArray(final int framAddress, final int size) throws IOException {
        if ((framAddress >= maxAddress) || ((framAddress + size - 1) >= maxAddress)) {
            throw new IOException("Memory address out of range");
        }
        if (size > MAX_PAYLOAD_SIZE_READ) {
            throw new IOException(String.format("Read payload size is bigger than %d", MAX_PAYLOAD_SIZE_READ));
        }

        final byte[] result = new byte[size];
        if (size == 0) {
            throw new IOException("Number of bytes asked to read is zero");
        } else {
            final byte[] address = adaptFRAMAddress(framAddress);
            device.write(address, address.length);

            device.read(result, result.length);

            if (VERBOSE) {
                Timber.i("Read %d bytes -> %s", result.length, print(result));
            }
        }
        return result;
    }

    // Writes a single byte to a specific address
    public void writeByte(final int framAddress, final byte value) throws IOException {
        final byte[] buffer = {value};

        writeArray(framAddress, buffer);
    }

    // Write a 16bits value (word) to a specific address
    public void writeWord(final int framAddress, final short value) throws IOException {
        final byte[] buffer = new byte[2];

        buffer[0] = (byte) (value & 0xff);
        buffer[1] = (byte) ((value >> 8) & 0xff);

        writeArray(framAddress, buffer);
    }

    // Write a 32bits value to the specified FRAM address
    public void writeInt(final int framAddress, final int value) throws IOException {
        final byte[] buffer = new byte[4];

        buffer[0] = (byte) (value >>> 24);
        buffer[1] = (byte) (value >>> 16);
        buffer[2] = (byte) (value >>> 8);
        buffer[3] = (byte) value;

        writeArray(framAddress, buffer);
    }

    // Writes an array of bytes to a specific address
    public void writeArray(final int framAddress, final byte[] value) throws IOException {
        if ((framAddress >= maxAddress) || ((framAddress + value.length - 1) >= maxAddress)) {
            throw new IOException("Memory address out of range");
        }
        if (value.length > MAX_PAYLOAD_SIZE_WRITE) {
            throw new IOException(String.format("Write payload size is bigger than %d", MAX_PAYLOAD_SIZE_WRITE));
        }

        final byte[] address = adaptFRAMAddress(framAddress);

        final byte[] buffer = new byte[address.length + value.length];
        System.arraycopy(address, 0, buffer, 0, address.length);
        System.arraycopy(value, 0, buffer, address.length, value.length);

        if (VERBOSE) {
            Timber.i("Write %d bytes -> %s", buffer.length, print(buffer));
        }

        device.write(buffer, buffer.length);
    }

    // Set one bit to the specified FRAM address
    public void setOneBit(final int framAddress, final int position) throws IOException {
        byte result;
        if (position > 7) {
            throw new IOException("Bit position out of range");
        } else {
            final byte[] buffer = readArray(framAddress, 1);
            buffer[0] = bitSet(buffer[0], position);
            writeArray(framAddress, buffer);
        }
    }

    // Clear one bit to the specified FRAM address
    public void clearOneBit(final int framAddress, final int position) throws IOException {
        if (position > 7) {
            throw new IOException("Bit position out of range");
        } else {
            final byte[] buffer = readArray(framAddress, 1);
            buffer[0] = bitClear(buffer[0], position);
            writeArray(framAddress, buffer);
        }
    }

    // Toggle one bit to the specified FRAM address
    public void toggleBit(final int framAddress, final int position) throws IOException {
        if (position > 7) {
            throw new IOException("Bit position out of range");
        } else {
            final byte[] buffer = readArray(framAddress, 1);

            if ((buffer[0] & (1 << position)) == (1 << position)) {
                buffer[0] = bitClear(buffer[0], position);
            } else {
                buffer[0] = bitSet(buffer[0], position);
            }

            writeArray(framAddress, buffer);
        }
    }

    // Copy a byte from one address to another in the memory scope
    public void copyByte(final int originalAddress, final int destinationAddress) throws IOException {
        final byte buffer = readByte(originalAddress);

        writeByte(destinationAddress, buffer);
    }

    // Erase device by overwriting it to 0x00
    public void erase() throws IOException {
        if (VERBOSE) {
            Timber.i("Start erasing device ->");
        }

        final int sectors = (maxAddress / MAX_PAYLOAD_SIZE_WRITE);
        if (sectors > 0) {
            for (int i = 0; i < sectors; i++) {
                writeArray(i * MAX_PAYLOAD_SIZE_WRITE, new byte[MAX_PAYLOAD_SIZE_WRITE]);
            }
            writeArray(MAX_PAYLOAD_SIZE_WRITE * sectors, new byte[2 * sectors]);
        } else {
            writeArray(0, new byte[maxAddress]);
        }

        if (VERBOSE) {
            Timber.i("<- device erased");
        }
    }


    private void connect(I2cDevice device) throws IOException {
        this.device = device;

        checkDevice();

        if (VERBOSE) {
            Timber.i("FRAM object created");
            Timber.i("I2C device address is 0x%02X", i2cAddress);
            Timber.i("Write protect pin number is %d", writeProtectPin);
            Timber.i("Write protect management: %s", String.valueOf(MANAGE_WRITE_PROTECT));
            Timber.i("Manufacturer 0x%02X", manufacturer);
            Timber.i("ProductId 0x%02X", productId);
            Timber.i("Density code 0x%02X", densityCode);
            Timber.i("Density %dK", density);

            if ((manufacturer != MANUALMODE_MANUFACT_ID) && (density > 0)) {
                Timber.i("Device properties automatically identified");
            }
            if ((manufacturer == MANUALMODE_MANUFACT_ID) && (density > 0)) {
                Timber.i("Device properties manually set");
            }
        }
    }

    // Check if device is connected at address
    // @returns 0 = device found or 7 = device not found
    private void checkDevice() throws IOException {
        if (manualMode) {
            setDeviceIDs();
        } else {
            getDeviceIDs();
        }

        if (((manufacturer == FUJITSU_MANUFACT_ID) || (manufacturer == CYPRESS_MANUFACT_ID) || (manufacturer == MANUALMODE_MANUFACT_ID)) && (maxAddress != 0)) {
            framInitialised = true;
        } else {
            framInitialised = false;
        }
    }

    @RequiresPermission("com.google.android.things.permission.USE_PERIPHERAL_IO")
    private void initGpioPin() throws IOException {
        gpioWriteProtect = PeripheralManager.getInstance().openGpio(writeProtectPin);
        gpioWriteProtect.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        gpioWriteProtect.setActiveType(Gpio.ACTIVE_HIGH);
    }

    // Init write protect function for class constructor
    private void initWriteProtect(final boolean shouldEnableWriteProtect) throws IOException {
        if (MANAGE_WRITE_PROTECT) {
            initGpioPin();
            if (shouldEnableWriteProtect) {
                enableWriteProtect();
            } else {
                disableWriteProtect();
            }
        } else {
            writeProtectStatus = false;
        }
    }

    // Set devices IDs for chip that does not support the feature as this has not been implemented in every chips by manufacturers
    private void setDeviceIDs() throws IOException {
        if (manualMode) {
            switch (density) {
                case DENSITY_4K:
                    maxAddress = MAXADDRESS_04;
                    break;
                case DENSITY_16K:
                    maxAddress = MAXADDRESS_16;
                    break;
                case DENSITY_64K:
                    maxAddress = MAXADDRESS_64;
                    break;
                case DENSITY_128K:
                    maxAddress = MAXADDRESS_128;
                    break;
                case DENSITY_256K:
                    maxAddress = MAXADDRESS_256;
                    break;
                case DENSITY_512K:
                    maxAddress = MAXADDRESS_512;
                    break;
                case DENSITY_1024K:
                    maxAddress = MAXADDRESS_1024;
                    break;
                default:
                    maxAddress = 0; /* means error */
                    break;
            }

            densityCode = MANUALMODE_DENSITY_ID;
            productId = MANUALMODE_PRODUCT_ID;
            manufacturer = MANUALMODE_MANUFACT_ID;
            if (maxAddress == 0) {
                throw new IOException("Fram chip unidentified");
            }
        } else {
            throw new IOException("Not permitted operation");
        }
    }

    // Reads the Manufacturer ID and the Product ID from the IC and populate class' variables for devices supporting that feature
    private void getDeviceIDs() throws IOException {
        byte[] localbuffer = {0, 0, 0};

        /* Get device IDs sequence 	*/
        /* 1/ Send 0xF8 to the I2C bus as a write instruction. bit 0: 0 => 0xF8 >> 1 */
        /* Send 0xF8 to 12C bus. Bit shift to right as beginTransmission() requires a 7bit. beginTransmission() 0 for write => 0xF8 */
        /* Send device address as 8 bits. Bit shift to left as we are using a simple write()                                        */
        /* Send 0xF9 to I2C bus. By requesting 3 bytes to read, requestFrom() add a 1 bit at the end of a 7 bits address => 0xF9    */
        /* See p.10 of http://www.fujitsu.com/downloads/MICRO/fsa/pdf/products/memory/fram/MB85RC-DS501-00017-3v0-E.pdf             */


//        final byte[] buffer = {(byte) MASTER_CODE >> 1, (byte) (i2cAddress << 1)}; //, (byte) 0xF9
//        device.write(buffer, buffer.length);

//        Wire.beginTransmission(MASTER_CODE >> 1);
//        Wire.write((byte)(i2cAddress << 1));
//        result = Wire.endTransmission(false);

//        device.read(localbuffer, localbuffer.length);
        device.write(new byte[]{(byte) 0xF8}, 1);
        device.write(new byte[]{(byte) i2cAddress}, 1);
        device.write(new byte[]{(byte) 0xF9}, 1);
        device.read(localbuffer, localbuffer.length);
//        Wire.requestFrom(MASTER_CODE >> 1, 3);
//        localbuffer[0] = (uint8_t) Wire.read();
//        localbuffer[1] = (uint8_t) Wire.read();
//        localbuffer[2] = (uint8_t) Wire.read();

        /* Shift values to separate IDs */
        manufacturer = (localbuffer[0] << 4) + (localbuffer[1] >> 4);
        densityCode = ((short) localbuffer[1] & 0x0F);
        productId = ((localbuffer[1] & 0x0F) << 8) + localbuffer[2];

        if (VERBOSE) {
            Timber.i("Device data - manufacturer: %d productId: %d densityCode: %d", manufacturer, productId, densityCode);
        }

        if (manufacturer == FUJITSU_MANUFACT_ID) {
            switch (densityCode) {
                case DENSITY_CODE_MB85RC04V:
                    density = DENSITY_4K;
                    maxAddress = MAXADDRESS_04;
                    break;
                case DENSITY_CODE_MB85RC64TA:
                    density = DENSITY_64K;
                    maxAddress = MAXADDRESS_64;
                    break;
                case DENSITY_CODE_MB85RC256V:
                    density = DENSITY_256K;
                    maxAddress = MAXADDRESS_256;
                    break;
                case DENSITY_CODE_MB85RC512T:
                    density = DENSITY_512K;
                    maxAddress = MAXADDRESS_512;
                    break;
                case DENSITY_CODE_MB85RC1MT:
                    density = DENSITY_1024K;
                    maxAddress = MAXADDRESS_1024;
                    break;
                default:
                    density = 0; /* means error */
                    maxAddress = 0; /* means error */
                    throw new IOException("Fram chip unidentified");
            }
        } else if (manufacturer == CYPRESS_MANUFACT_ID) {
            switch (densityCode) {
                case DENSITY_CODE_CY15B128J:
                    density = DENSITY_128K;
                    maxAddress = MAXADDRESS_128;
                    break;
                case DENSITY_CODE_CY15B256J:
                    density = DENSITY_256K;
                    maxAddress = MAXADDRESS_256;
                    break;
                case DENSITY_CODE_FM24V05:
                    density = DENSITY_512K;
                    maxAddress = MAXADDRESS_512;
                    break;
                case DENSITY_CODE_FM24V10:
                    density = DENSITY_1024K;
                    maxAddress = MAXADDRESS_1024;
                    break;
                default:
                    density = 0; /* means error */
                    maxAddress = 0; /* means error */
                    throw new IOException("Fram chip unidentified");
            }
        } else {
            density = 0; /* means error */
            maxAddress = 0; /* means error */
            throw new IOException("Fram chip unidentified");

        }
    }

    // Adapts the I2C calls (chip address + memory pointer) according to chip datasheet
    // 4K chips : 1 MSB of memory address as LSB of device address + 8 bits memory address
    // 16K chips : 3 MSB of memory address as LSB of device address + 8 bits memory address
    // 64K and more chips : full chipp address & 16 bits memory address
    private byte[] adaptFRAMAddress(final int framAddress) throws IOException {
        if (framAddress >= maxAddress) {
            throw new IOException("Memory address out of range");
        }

        if (density < DENSITY_64K) {
            return new byte[]{lsb(framAddress)};
        } else {
            return new byte[]{msb(framAddress), lsb(framAddress)};
        }
    }

    private byte msb(final int framAddress) {
        return (byte) ((framAddress >> 8) & 0xFF);
    }

    private byte lsb(final int framAddress) {
        return (byte) (framAddress & 0xFF);
    }

    private byte bitRead(final byte bufferedByte, final int position) {
        return (byte) ((bufferedByte >> position) & 1);
    }

    private byte bitSet(final byte bufferedByte, final int position) {
        return (byte) (bufferedByte | 1 << position);
    }

    private byte bitClear(final byte bufferedByte, final int position) {
        return (byte) (bufferedByte & ~(1 << position));
    }

    private String print(final byte[] bytes) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ ");
        for (byte b : bytes) {
            stringBuilder.append(String.format("0x%02X ", b));
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

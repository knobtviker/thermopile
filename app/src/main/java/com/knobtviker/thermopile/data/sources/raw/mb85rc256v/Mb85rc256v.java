package com.knobtviker.thermopile.data.sources.raw.mb85rc256v;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by bojan on 18/01/2018.
 */

public class Mb85rc256v implements Closeable {
    /**
     * Chip vendor for the MB85RC256V
     */
    public static final String CHIP_VENDOR = "Maxim Integrated Products";

    /**
     * Chip name for the MB85RC256V
     */
    public static final String CHIP_NAME = "DS3231";

    /**
     * I2C address for this peripheral
     */
    public static final int I2C_ADDRESS = 0x50;

    public static final int MB85RC_SLAVE_ID = 0xF8;

    private final int manufacturerIdFujitsu = 0x00A;

    private final int productId = 0x510;

    private boolean isInitialized = false;

    private I2cDevice device;

    /**
     * Create a new MB85RC256V driver connected on the given bus.
     *
     * @param bus I2C bus the sensor is connected to.
     * @throws IOException
     */
    public Mb85rc256v(String bus) throws IOException {
        this(bus, I2C_ADDRESS);
    }

    /**
     * Create a new MB85RC256V driver connected on the given bus and address.
     *
     * @param bus     I2C bus the sensor is connected to.
     * @param address I2C address of the sensor.
     * @throws IOException
     */
    public Mb85rc256v(String bus, int address) throws IOException {
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
     * Create a new MB85RC256V driver connected to the given I2c device.
     *
     * @param device I2C device of the sensor.
     * @throws IOException
     */
    /*package*/  Mb85rc256v(I2cDevice device) throws IOException {
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

    private void connect(I2cDevice device) throws IOException {
        this.device = device;
    }

    /*
    @brief  Writes a byte at the specific FRAM address

    @params[in] i2cAddr
                The I2C address of the FRAM memory chip (1010+A2+A1+A0)
    @params[in] framAddr
                The 16-bit address to write to in FRAM memory
    @params[in] i2cAddr
                The 8-bit value to write at framAddr
*/
    public void writeByte(final int framAddress, final byte value) throws IOException {
        device.writeRegByte(framAddress, value);
    }

    /*
    @brief  Reads an 8 bit value from the specified FRAM address
    @params[in] i2cAddr
                The I2C address of the FRAM memory chip (1010+A2+A1+A0)
    @params[in] framAddr
                The 16-bit address to read from in FRAM memory
    @returns    The 8-bit value retrieved at framAddress
*/
    public byte readByte(final int framAddress) throws IOException {
        return device.readRegByte(framAddress);
    }

    public void writeBytes(final int framAddress, final int length, final byte[] data) throws IOException {
        final int addressLow = framAddress; // lower part of the address
        final int addressHigh = framAddress >> 8; // higher part of the address

        final byte[] values = new byte[length + 1];

        values[0] = (byte)addressLow;

        System.arraycopy(data, 0, values, 1, length);

        device.writeRegBuffer(addressHigh, values, values.length);
    }

    public byte[] readBytes(final int framAddress, final int length) throws IOException {
        final int addressLow = framAddress; // lower part of the address
        final int addressHigh = framAddress >> 8; // higher part of the address

        final byte[] values = new byte[length];

        device.readRegBuffer(addressHigh, values, length);

        return values;
    }

    //https://github.com/adafruit/Adafruit_FRAM_I2C/issues/2
    //here are buffer-oriented read and write functions, for uint8_t-sized buffers.
    //They gracefully handle buffers larger than the i2c bus buffer size.
    /*
    uint8_t readn(uint16_t framAddr, uint8_t n, uint8_t *p)
{
  uint8_t i = 0;
  for(i = 0; i < n; ) {
    Wire.beginTransmission(0x50);
    Wire.write(framAddr >> 8);
    Wire.write(framAddr & 0xFF);
    Wire.endTransmission();
    uint8_t req;
    req = n;
    if(n > 32) {
      req = 32;
    }
    Wire.requestFrom(uint8_t(0x50), req);
    uint8_t j;
    for (j = 0; j < 32 && i < n; i++, j++) {
      if(!Wire.available()) {
        return i;
      }
      *p++ = Wire.read();
    }
    framAddr += j;
  }
  return i;
}

void writen(uint16_t framAddr, uint8_t n, uint8_t *p)
{
  uint8_t i = 0;
  for(i = 0; i < n; ) {
    Wire.beginTransmission(0x50);
    Wire.write(framAddr >> 8);
    Wire.write(framAddr & 0xFF);
    uint8_t j;
    for (j = 0; j < 30 && i < n; i++, j++) {
      Wire.write(*p++);
    }
    Wire.endTransmission();
    framAddr += j;
  }
}
     */

    // Clear all registers
    public void clear() throws IOException {
        final int kilobytes = 32;
        for (int address = 0; address < kilobytes * 1024; address++) {
            writeByte(address, (byte) 0x00);
        }
    }

    // Dump the entire memory! Columns are kilobytes. Rows are bytes in that kilobyte.
    @SuppressLint("DefaultLocale")
    public void dump() throws IOException {
        final String TAG = "_";
        final int kilobytes = 32;
        final char[] boundary = new char[kilobytes * 5 + 1];
        for (int i = 0; i < kilobytes * 5 + 1; i++) {
            boundary[i] = i == 0 ? '+' : '-';
        }

        final StringBuilder stringBuilderHeader = new StringBuilder();
        final StringBuilder stringBuilderBoundary = new StringBuilder();
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilderHeader.append("KB -> |");
        for (int j = 0; j < kilobytes; j++) {
            stringBuilderHeader.append(String.format("   %02d", j));
        }
        stringBuilderHeader.append("\n");

        stringBuilderBoundary.append("------");
        stringBuilderBoundary.append(boundary);

        Log.i(TAG, stringBuilderHeader.toString());
        Log.i(TAG, stringBuilderBoundary.toString());

        for (int k = 0; k < 1024; k++) {
            stringBuilder.setLength(0);
            stringBuilder.append(String.format(" %04d | ", k));
            for (int l = 0; l < kilobytes; l++) {
                final byte value = readByte((l * 1024) + k);
                stringBuilder.append(String.format("0x%02X ", value < 0x1 ? 0 : value));
            }
            stringBuilder.append("\n");

            Log.i(TAG, stringBuilder.toString());
        }

        Log.i(TAG, stringBuilderBoundary.toString());
    }

    // Reads the Manufacturer ID and the Product ID from chip.
    // The 12-bit manufacturer ID (Fujitsu = 0x00A)
    // The memory density (bytes 11..8) and proprietary Product ID fields (bytes 7..0). Should be 0x510 for the MB85RC256V.
    private void deviceId() throws IOException {
//        int a[3] = { 0, 0, 0 };
//        uint8_t results;
//
        writeByte(MB85RC_SLAVE_ID >> 1, (byte) (I2C_ADDRESS << 1));
//        Wire.beginTransmission(MB85RC_SLAVE_ID >> 1);
//        Wire.write(i2c_addr << 1);
//        results = Wire.endTransmission(false);
//
//        Wire.requestFrom(MB85RC_SLAVE_ID >> 1, 3);
//        a[0] = Wire.read();
//        a[1] = Wire.read();
//        a[2] = Wire.read();
//
//  /* Shift values to separate manuf and prod IDs */
//  /* See p.10 of http://www.fujitsu.com/downloads/MICRO/fsa/pdf/products/memory/fram/MB85RC256V-DS501-00017-3v0-E.pdf */
//  *manufacturerID = (a[0] << 4) + (a[1]  >> 4);
//  *productID = ((a[1] & 0x0F) << 8) + a[2];
    }
}

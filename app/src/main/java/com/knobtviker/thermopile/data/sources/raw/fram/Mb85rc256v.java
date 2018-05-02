package com.knobtviker.thermopile.data.sources.raw.fram;

import android.support.annotation.NonNull;

import java.io.IOException;

public class Mb85rc256v extends Fram {

    /**
     * Chip vendor for the MB85RC256V
     */
    public static final String CHIP_VENDOR = "Fujitsu";

    /**
     * Chip name for the MB85RC256V
     */
    public static final String CHIP_NAME = "MB85RC256V";

    /**
     * I2C address for this peripheral
     */
    public static final int I2C_ADDRESS = 0x50;

    public Mb85rc256v(@NonNull final String bus) throws IOException {
        super(bus, I2C_ADDRESS, Fram.DEFAULT_WRITE_PROTECT_PIN, Fram.DEFAULT_WRITE_PROTECT_STATUS, Fram.DENSITY_256K);
    }

    public Mb85rc256v(@NonNull final String bus, @NonNull final String writeProtectPin, final boolean writeProtectStatus) throws IOException {
        super(bus, I2C_ADDRESS, writeProtectPin, writeProtectStatus, Fram.DENSITY_256K);
    }
}

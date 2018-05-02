package com.knobtviker.thermopile.data.sources.memory.implementation;

import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.thermopile.data.sources.raw.fram.Mb85rc256v;

import java.io.IOException;

import timber.log.Timber;

public abstract class AbstractMemoryDataSource {

    private int ADDRESS_INVALID = -1;
    public static int ADDRESS_TEMPERATURE = 0;
    public static int ADDRESS_PRESSURE = 4;
    public static int ADDRESS_HUMIDITY = 8;
    public static int ADDRESS_ALTITUDE = 12;
    public static int ADDRESS_AIR_QUALITY = 16;
    public static int ADDRESS_LUMINOSITY = 20;
    public static int ADDRESS_ACCELERATION_X = 24;
    public static int ADDRESS_ACCELERATION_Y = 28;
    public static int ADDRESS_ACCELERATION_Z = 32;
    public static int ADDRESS_ANGULAR_VELOCITY_X = 36;
    public static int ADDRESS_ANGULAR_VELOCITY_Y = 40;
    public static int ADDRESS_ANGULAR_VELOCITY_Z = 44;
    public static int ADDRESS_MAGNETIC_FIELD_X = 48;
    public static int ADDRESS_MAGNETIC_FIELD_Y = 52;
    public static int ADDRESS_MAGNETIC_FIELD_Z = 56;

    protected int address = ADDRESS_INVALID;
    protected int addressX = ADDRESS_INVALID;
    protected int addressY = ADDRESS_INVALID;
    protected int addressZ = ADDRESS_INVALID;

    private Mb85rc256v fram = null;

    public AbstractMemoryDataSource() {
        try {
            fram = new Mb85rc256v(BoardDefaults.defaultI2CBus());
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    protected void saveInt(final int address, final int value) throws IOException {
        if (address == ADDRESS_INVALID) {
            throw new IOException("No address assigned");
        }

        fram.writeInt(address, value);
    }

    protected int readInt(final int address) throws IOException {
        if (address == ADDRESS_INVALID) {
            throw new IOException("No address assigned");
        }

        return fram.readInt(address);
    }
}

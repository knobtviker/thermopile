package com.knobtviker.thermopile.data.sources.raw;

import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.thermopile.data.sources.PeripheralsDataSource;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class PeripheralRawDataSource implements PeripheralsDataSource.Raw {

    @Inject
    public PeripheralRawDataSource() {
    }

    @Override
    public List<I2CDevice> load() {
        return BoardDefaults.i2CDevices();
    }
}

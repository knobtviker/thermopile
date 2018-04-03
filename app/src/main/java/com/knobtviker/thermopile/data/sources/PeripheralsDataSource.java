package com.knobtviker.thermopile.data.sources;

import com.knobtviker.android.things.contrib.community.boards.I2CDevice;

import java.util.List;

/**
 * Created by bojan on 18/07/2017.
 */

public interface PeripheralsDataSource {

    interface Raw {

        List<I2CDevice> load();
    }
}

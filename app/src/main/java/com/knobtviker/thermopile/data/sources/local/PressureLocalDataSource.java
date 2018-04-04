package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.sources.local.implemenatation.SensorLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class PressureLocalDataSource extends SensorLocalDataSource<Pressure> {

    @Inject
    public PressureLocalDataSource() {
        super(Pressure.class);
    }
}

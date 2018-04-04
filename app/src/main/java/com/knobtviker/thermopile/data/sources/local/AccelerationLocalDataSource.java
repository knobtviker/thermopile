package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.sources.local.implemenatation.SensorLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class AccelerationLocalDataSource extends SensorLocalDataSource<Acceleration> {

    @Inject
    public AccelerationLocalDataSource() {
        super(Acceleration.class);
    }
}

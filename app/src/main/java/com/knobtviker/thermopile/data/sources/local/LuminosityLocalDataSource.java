package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Luminosity;
import com.knobtviker.thermopile.data.sources.local.implemenatation.SensorLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class LuminosityLocalDataSource extends SensorLocalDataSource<Luminosity> {

    @Inject
    public LuminosityLocalDataSource() {
        super(Luminosity.class);
    }
}

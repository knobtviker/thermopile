package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.sources.local.implemenatation.SensorLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class HumidityLocalDataSource extends SensorLocalDataSource<Humidity> {

    @Inject
    public HumidityLocalDataSource() {
        super(Humidity.class);
    }
}

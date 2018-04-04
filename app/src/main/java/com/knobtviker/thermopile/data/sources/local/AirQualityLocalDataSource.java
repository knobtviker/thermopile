package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.sources.local.implemenatation.SensorLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class AirQualityLocalDataSource extends SensorLocalDataSource<AirQuality> {

    @Inject
    public AirQualityLocalDataSource() {
        super(AirQuality.class);
    }
}

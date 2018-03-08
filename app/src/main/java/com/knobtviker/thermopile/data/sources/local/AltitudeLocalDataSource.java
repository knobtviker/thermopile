package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.sources.local.implemenatation.AbstractLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class AltitudeLocalDataSource extends AbstractLocalDataSource<Altitude> {

    @Inject
    public AltitudeLocalDataSource() {
        super(Altitude.class);
    }
}

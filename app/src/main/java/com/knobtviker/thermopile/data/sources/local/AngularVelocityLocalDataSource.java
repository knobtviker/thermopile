package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.sources.local.implemenatation.SensorLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class AngularVelocityLocalDataSource extends SensorLocalDataSource<AngularVelocity> {

    @Inject
    public AngularVelocityLocalDataSource() {
        super(AngularVelocity.class);
    }
}

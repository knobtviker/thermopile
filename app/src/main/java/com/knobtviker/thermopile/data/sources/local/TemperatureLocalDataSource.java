package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.implemenatation.AbstractLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class TemperatureLocalDataSource extends AbstractLocalDataSource<Temperature> {

    @Inject
    public TemperatureLocalDataSource() {
        super(Temperature.class);
    }
}

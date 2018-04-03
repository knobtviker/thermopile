package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.sources.local.implemenatation.AbstractReadOnlyLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class PeripheralLocalDataSource extends AbstractReadOnlyLocalDataSource<PeripheralDevice> {

    @Inject
    public PeripheralLocalDataSource() {
        super(PeripheralDevice.class);
    }
}

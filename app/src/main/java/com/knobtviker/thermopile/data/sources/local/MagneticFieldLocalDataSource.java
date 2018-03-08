package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.sources.local.implemenatation.AbstractLocalDataSource;

import javax.inject.Inject;

/**
 * Created by bojan on 26/06/2017.
 */

public class MagneticFieldLocalDataSource extends AbstractLocalDataSource<MagneticField> {

    @Inject
    public MagneticFieldLocalDataSource() {
        super(MagneticField.class);
    }
}

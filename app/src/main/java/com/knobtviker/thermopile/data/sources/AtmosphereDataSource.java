package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;

/**
 * Created by bojan on 18/07/2017.
 */

public interface AtmosphereDataSource {

    interface Local {

        void save(@NonNull final Atmosphere item);
    }
}

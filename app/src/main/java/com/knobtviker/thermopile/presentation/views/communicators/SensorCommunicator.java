package com.knobtviker.thermopile.presentation.views.communicators;

import android.support.annotation.NonNull;

/**
 * Created by bojan on 12/11/2017.
 */

public interface SensorCommunicator {

    void onSensorChecked(@NonNull final String primaryKey, final int type, final boolean isChecked);
}

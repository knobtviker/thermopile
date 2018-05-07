package com.knobtviker.thermopile.presentation.views.communicators;

/**
 * Created by bojan on 12/11/2017.
 */

public interface SensorCommunicator {

    void onSensorChecked(final long id, final int type, final boolean isChecked);
}

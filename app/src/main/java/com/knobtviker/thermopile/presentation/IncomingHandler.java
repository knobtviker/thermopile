package com.knobtviker.thermopile.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.views.communicators.IncomingCommunicator;
import com.knobtviker.thermopile.shared.constants.Keys;
import com.knobtviker.thermopile.shared.constants.Uid;

import java.util.Objects;

import javax.inject.Inject;

public class IncomingHandler extends Handler {

    @NonNull
    private final IncomingCommunicator incomingCommunicator;

    @Inject
    public IncomingHandler(@NonNull final Context context) {
        this.incomingCommunicator = (IncomingCommunicator) context;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Uid.DRIVERS:
                handleDriverMessage(message);
                break;
            case Uid.FRAM:
                handleFramMessage(message);
                break;
            case Uid.INVALID:
            default:
                super.handleMessage(message);
        }
    }

    private void handleDriverMessage(@NonNull final Message message) {
        final Bundle data = message.getData();

        if (data.containsKey(Keys.VENDOR) && data.containsKey(Keys.NAME)) {
            if (data.containsKey(Keys.TEMPERATURE)) {
                incomingCommunicator.saveTemperature(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloat(Keys.TEMPERATURE)
                );
            }
            if (data.containsKey(Keys.PRESSURE)) {
                incomingCommunicator.savePressure(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloat(Keys.PRESSURE)
                );
            }
            if (data.containsKey(Keys.HUMIDITY)) {
                incomingCommunicator.saveHumidity(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloat(Keys.HUMIDITY)
                );
            }
            if (data.containsKey(Keys.AIR_QUALITY)) {
                incomingCommunicator.saveAirQuality(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloat(Keys.AIR_QUALITY)
                );
            }
            if (data.containsKey(Keys.LUMINOSITY)) {
                incomingCommunicator.saveLuminosity(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloat(Keys.LUMINOSITY)
                );
            }
            if (data.containsKey(Keys.ACCELERATION)) {
                incomingCommunicator.saveAcceleration(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloatArray(Keys.ACCELERATION)
                );
            }
            if (data.containsKey(Keys.ANGULAR_VELOCITY)) {
                incomingCommunicator.saveAngularVelocity(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloatArray(Keys.ANGULAR_VELOCITY)
                );
            }
            if (data.containsKey(Keys.MAGNETIC_FIELD)) {
                incomingCommunicator.saveMagneticField(
                    Objects.requireNonNull(data.getString(Keys.VENDOR)),
                    Objects.requireNonNull(data.getString(Keys.NAME)),
                    data.getFloatArray(Keys.MAGNETIC_FIELD)
                );
            }
        } else {
            super.handleMessage(message);
        }
    }

    private void handleFramMessage(@NonNull final Message message) {
        long lastBootTimestamp = 0L;
        long bootCount = 1l;

        final Bundle data = message.getData();
        if (data.containsKey(Keys.LAST_BOOT_TIMESTAMP)) {
            lastBootTimestamp = data.getLong(Keys.LAST_BOOT_TIMESTAMP);
            incomingCommunicator.setLastBootTimestamp(lastBootTimestamp);
        }
        if (data.containsKey(Keys.BOOT_COUNT)) {
            bootCount = data.getLong(Keys.BOOT_COUNT);
            incomingCommunicator.setBootCount(bootCount);
        }
    }
}

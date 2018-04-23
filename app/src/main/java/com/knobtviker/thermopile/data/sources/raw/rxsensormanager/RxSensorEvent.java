package com.knobtviker.thermopile.data.sources.raw.rxsensormanager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public final class RxSensorEvent {
    private SensorEvent sensorEvent;
    private Sensor sensor;
    private int accuracy = -1;

    public RxSensorEvent(SensorEvent sensorEvent) {
        this.sensorEvent = sensorEvent;
    }

    public RxSensorEvent(Sensor sensor, int accuracy) {
        this.sensor = sensor;
        this.accuracy = accuracy;
    }

    public SensorEvent getSensorEvent() {
        return sensorEvent;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public boolean hasSensorEvent() {
        return sensorEvent != null;
    }

    public boolean hasAccuracy() {
        return sensor != null && accuracy != -1;
    }
}

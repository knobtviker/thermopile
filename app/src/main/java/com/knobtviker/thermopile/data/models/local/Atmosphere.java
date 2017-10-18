package com.knobtviker.thermopile.data.models.local;

import io.realm.RealmObject;

/**
 * Created by bojan on 15/06/2017.
 */

public class Atmosphere extends RealmObject {

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public float temperature() {
        return temperature;
    }

    public void temperature(final float temperature) {
        this.temperature = temperature;
    }

    public float humidity() {
        return humidity;
    }

    public void humidity(final float humidity) {
        this.humidity = humidity;
    }

    public float pressure() {
        return pressure;
    }

    public void pressure(final float pressure) {
        this.pressure = pressure;
    }

    private long timestamp;

    private float temperature;

    private float humidity;

    private float pressure;
}

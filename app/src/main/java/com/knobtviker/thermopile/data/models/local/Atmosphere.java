package com.knobtviker.thermopile.data.models.local;

import io.realm.RealmObject;

/**
 * Created by bojan on 27/12/2017.
 */

public class Atmosphere extends RealmObject {

    public Temperature temperature() {
        return temperature;
    }

    public void temperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Humidity humidity() {
        return humidity;
    }

    public void humidity(Humidity humidity) {
        this.humidity = humidity;
    }

    public Pressure pressure() {
        return pressure;
    }

    public void pressure(Pressure pressure) {
        this.pressure = pressure;
    }

    public AirQuality airQuality() {
        return airQuality;
    }

    public void airQuality(AirQuality airQuality) {
        this.airQuality = airQuality;
    }

    public Altitude altitude() {
        return altitude;
    }

    public void altitude(Altitude altitude) {
        this.altitude = altitude;
    }

    private Temperature temperature;

    private Humidity humidity;

    private Pressure pressure;

    private AirQuality airQuality;

    private Altitude altitude;
}

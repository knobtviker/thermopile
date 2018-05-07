package com.knobtviker.thermopile.data.models.local;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.DeviceModel;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PeripheralDevice extends DeviceModel {

    @SuppressLint("DefaultLocale")
    public PeripheralDevice(final int address, @NonNull final String bus, @NonNull final String vendor, @NonNull final String name,
                            final boolean hasTemperature, final boolean hasPressure, final boolean hasHumidity, final boolean hasAirQuality,
                            final boolean hasLuminosity, final boolean hasAcceleration, final boolean hasAngularVelocity, final boolean hasMagneticField) {
        this.address = address;
        this.bus = bus;
        this.vendor = vendor;
        this.name = name;
        this.connected = false;
        this.hasTemperature = hasTemperature;
        this.hasPressure = hasPressure;
        this.hasHumidity = hasHumidity;
        this.hasAirQuality = hasAirQuality;
        this.hasLuminosity = hasLuminosity;
        this.hasAcceleration = hasAcceleration;
        this.hasAngularVelocity = hasAngularVelocity;
        this.hasMagneticField = hasMagneticField;
        this.enabledTemperature = hasTemperature;
        this.enabledPressure = hasPressure;
        this.enabledHumidity = hasHumidity;
        this.enabledAirQuality = hasAirQuality;
        this.enabledLuminosity = hasLuminosity;
        this.enabledAcceleration = hasAcceleration;
        this.enabledAngularVelocity = hasAngularVelocity;
        this.enabledMagneticField = hasMagneticField;
        this.id = (long) Objects.hash(bus, address);
    }

    @Id(assignable = true)
    public long id;

    public boolean enabledTemperature;

    public boolean enabledPressure;

    public boolean enabledHumidity;

    public boolean enabledAirQuality;

    public boolean enabledLuminosity;

    public boolean enabledAcceleration;

    public boolean enabledAngularVelocity;

    public boolean enabledMagneticField;

    public boolean hasTemperature;

    public boolean hasPressure;

    public boolean hasHumidity;

    public boolean hasAirQuality;

    public boolean hasLuminosity;

    public boolean hasAcceleration;

    public boolean hasAngularVelocity;

    public boolean hasMagneticField;
}

package com.knobtviker.thermopile.data.models.local;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.DeviceModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PeripheralDevice extends RealmObject implements DeviceModel {

    @SuppressLint("DefaultLocale")
    public PeripheralDevice(final int address, @NonNull final String bus, @NonNull final String vendor, @NonNull final String name,
                            final boolean hasTemperature, final boolean hasPressure, final boolean hasHumidity, final boolean hasAirQuality,
                            final boolean hasLuminosity, final boolean hasAcceleration, final boolean hasAngularVelocity, final boolean hasMagneticField) {
        address(address);
        bus(bus);
        vendor(vendor);
        name(name);
        connected(false);
        hasTemperature(hasTemperature);
        hasPressure(hasPressure);
        hasHumidity(hasHumidity);
        hasAirQuality(hasAirQuality);
        hasLuminosity(hasLuminosity);
        hasAcceleration(hasAcceleration);
        hasAngularVelocity(hasAngularVelocity);
        hasMagneticField(hasMagneticField);
        enabledTemperature(hasTemperature);
        enabledPressure(hasPressure);
        enabledHumidity(hasHumidity);
        enabledAirQuality(hasAirQuality);
        enabledLuminosity(hasLuminosity);
        enabledAcceleration(hasAcceleration);
        enabledAngularVelocity(hasAngularVelocity);
        enabledMagneticField(hasMagneticField);
        primaryKey(String.format("%s_%d", bus, address));
    }

    public PeripheralDevice() {}

    @PrimaryKey
    private String primaryKey;

    private int address;

    private String vendor;

    private String name;

    private String bus;

    private boolean connected;

    private boolean enabledTemperature;

    private boolean enabledPressure;

    private boolean enabledHumidity;

    private boolean enabledAirQuality;

    private boolean enabledLuminosity;

    private boolean enabledAcceleration;

    private boolean enabledAngularVelocity;

    private boolean enabledMagneticField;

    private boolean hasTemperature;

    private boolean hasPressure;

    private boolean hasHumidity;

    private boolean hasAirQuality;

    private boolean hasLuminosity;

    private boolean hasAcceleration;

    private boolean hasAngularVelocity;

    private boolean hasMagneticField;

    @Override
    public String vendor() {
        return vendor;
    }

    @Override
    public void vendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void name(String name) {
        this.name = name;
    }

    @Override
    public String bus() {
        return bus;
    }

    @Override
    public void bus(String bus) {
        this.bus = bus;
    }

    @Override
    public int address() {
        return address;
    }

    @Override
    public void address(int address) {
        this.address = address;
    }

    @Override
    public boolean connected() {
        return connected;
    }

    @Override
    public void connected(boolean connected) {
        this.connected = connected;
    }

    public boolean enabledTemperature() {
        return enabledTemperature;
    }

    public void enabledTemperature(boolean enabled) {
        this.enabledTemperature = enabled;
    }

    public boolean enabledPressure() {
        return enabledPressure;
    }

    public void enabledPressure(boolean enabled) {
        this.enabledPressure = enabled;
    }

    public boolean enabledHumidity() {
        return enabledHumidity;
    }

    public void enabledHumidity(boolean enabled) {
        this.enabledHumidity = enabled;
    }

    public boolean enabledAirQuality() {
        return enabledAirQuality;
    }

    public void enabledAirQuality(boolean enabled) {
        this.enabledAirQuality = enabled;
    }

    public boolean enabledLuminosity() {
        return enabledLuminosity;
    }

    public void enabledLuminosity(boolean enabled) {
        this.enabledLuminosity = enabled;
    }

    public boolean enabledAcceleration() {
        return enabledAcceleration;
    }

    public void enabledAcceleration(boolean enabled) {
        this.enabledAcceleration = enabled;
    }

    public boolean enabledAngularVelocity() {
        return enabledAngularVelocity;
    }

    public void enabledAngularVelocity(boolean enabled) {
        this.enabledAngularVelocity = enabled;
    }

    public boolean enabledMagneticField() {
        return enabledMagneticField;
    }

    public void enabledMagneticField(boolean enabled) {
        this.enabledMagneticField = enabled;
    }

    public boolean hasTemperature() {
        return hasTemperature;
    }

    public void hasTemperature(boolean hasTemperature) {
        this.hasTemperature = hasTemperature;
    }

    public boolean hasPressure() {
        return hasPressure;
    }

    public void hasPressure(boolean hasPressure) {
        this.hasPressure = hasPressure;
    }

    public boolean hasHumidity() {
        return hasHumidity;
    }

    public void hasHumidity(boolean hasHumidity) {
        this.hasHumidity = hasHumidity;
    }

    public boolean hasAirQuality() {
        return hasAirQuality;
    }

    public void hasAirQuality(boolean hasAirQuality) {
        this.hasAirQuality = hasAirQuality;
    }

    public boolean hasLuminosity() {
        return hasLuminosity;
    }

    public void hasLuminosity(boolean hasLuminosity) {
        this.hasLuminosity = hasLuminosity;
    }

    public boolean hasAcceleration() {
        return hasAcceleration;
    }

    public void hasAcceleration(boolean hasAcceleration) {
        this.hasAcceleration = hasAcceleration;
    }

    public boolean hasAngularVelocity() {
        return hasAngularVelocity;
    }

    public void hasAngularVelocity(boolean hasAngularVelocity) {
        this.hasAngularVelocity = hasAngularVelocity;
    }

    public boolean hasMagneticField() {
        return hasMagneticField;
    }

    public void hasMagneticField(boolean hasMagneticField) {
        this.hasMagneticField = hasMagneticField;
    }

    public String primaryKey() {
        return primaryKey;
    }

    private void primaryKey(@NonNull final String primaryKey) {
        this.primaryKey = primaryKey;
    }
}

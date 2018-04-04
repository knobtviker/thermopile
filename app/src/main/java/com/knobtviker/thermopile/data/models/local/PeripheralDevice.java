package com.knobtviker.thermopile.data.models.local;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.DeviceModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PeripheralDevice extends RealmObject implements DeviceModel {

    public PeripheralDevice(final int address, @NonNull final String bus, @NonNull final String vendor, @NonNull final String name) {
        address(address);
        bus(bus);
        vendor(vendor);
        name(name);
        enabled(false);
        connected(false);
        setPrimaryKey();
    }

    public PeripheralDevice() {}

    @SuppressWarnings("FieldCanBeLocal")
    @PrimaryKey
    private String primaryKey;

    private int address;

    private String vendor;

    private String name;

    private String bus;

    private boolean enabled;

    private boolean connected;

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
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean connected() {
        return connected;
    }

    @Override
    public void connected(boolean connected) {
        this.connected = connected;
    }

    @SuppressLint("DefaultLocale")
    private void setPrimaryKey() {
        this.primaryKey = String.format("%s_%d", this.bus, this.address);
    }
}

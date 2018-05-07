package com.knobtviker.thermopile.data.models.local.implementation;

import io.objectbox.annotation.BaseEntity;

@BaseEntity
public abstract class DeviceModel {

    public String vendor;

    public String name;

    public String bus;

    public int address;

    public boolean connected;
}

package com.knobtviker.thermopile.data.models.local;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by bojan on 27/12/2017.
 */

public class Humidity extends RealmObject {

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float value() {
        return value;
    }

    public void value(float value) {
        this.value = value;
    }

    public int accuracy() {
        return accuracy;
    }

    public void accuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String vendor() {
        return vendor;
    }

    public void vendor(String vendor) {
        this.vendor = vendor;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    @Index
    private long timestamp;

    private float value;

    private int accuracy;

    private String vendor;

    private String name;
}

package com.knobtviker.thermopile.data.models.local;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by bojan on 27/12/2017.
 */

public class MagneticField extends RealmObject {

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float valueX() {
        return valueX;
    }

    public void valueX(float valueX) {
        this.valueX = valueX;
    }

    public float valueY() {
        return valueY;
    }

    public void valueY(float valueY) {
        this.valueY = valueY;
    }

    public float valueZ() {
        return valueZ;
    }

    public void valueZ(float valueZ) {
        this.valueZ = valueZ;
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

    private float valueX;

    private float valueY;

    private float valueZ;

    private int accuracy;

    private String vendor;

    private String name;
}

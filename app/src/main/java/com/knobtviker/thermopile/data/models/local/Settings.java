package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bojan on 15/06/2017.
 */

public class Settings extends RealmObject {

    public String timezone() {
        return timezone;
    }

    public void timezone(@NonNull final String timezone) {
        this.timezone = timezone;
    }

    public int formatClock() {
        return formatClock;
    }

    public void formatClock(final int formatClock) {
        this.formatClock = formatClock;
    }

    public int formatDate() {
        return formatDate;
    }

    public void formatDate(final int formatDate) {
        this.formatDate = formatDate;
    }

    public int formatTime() {
        return formatTime;
    }

    public void formatTime(final int formatTime) {
        this.formatTime = formatTime;
    }

    public int unitTemperature() {
        return unitTemperature;
    }

    public void unitTemperature(final int unitTemperature) {
        this.unitTemperature = unitTemperature;
    }

    public int unitPressure() {
        return unitPressure;
    }

    public void unitPressure(final int unitPressure) {
        this.unitPressure = unitPressure;
    }

    @PrimaryKey
    private long id = 0; //TODO: Hardcoded to 0 but should be userId for multiuser support. Every user has it's owm settings record

    private String timezone;

    private int formatClock;

    private int formatDate;

    private int formatTime;

    private int unitTemperature;

    private int unitPressure;
}

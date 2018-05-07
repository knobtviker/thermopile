package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.BaseModel;

import io.objectbox.annotation.Entity;

/**
 * Created by bojan on 15/06/2017.
 */

@Entity
public class Settings extends BaseModel {

    public Settings(@NonNull final String timezone, final int formatClock, @NonNull final String formatDate, @NonNull final String formatTime, final int unitTemperature, final int unitPressure, final int unitMotion, final int screensaverDelay, final int theme) {
        this.timezone = timezone;
        this.formatClock = formatClock;
        this.formatDate = formatDate;
        this.formatTime = formatTime;
        this.unitTemperature = unitTemperature;
        this.unitPressure = unitPressure;
        this.unitMotion = unitMotion;
        this.screensaverDelay = screensaverDelay;
        this.theme = theme;
    }

    public String timezone;

    public int formatClock;

    public String formatDate;

    public String formatTime;

    public int unitTemperature;

    public int unitPressure;

    public int unitMotion;

    public int screensaverDelay;

    public int theme;
}

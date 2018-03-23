package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.implementation.BaseModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bojan on 15/06/2017.
 */

public class Settings extends RealmObject implements BaseModel {

    public Settings(final long id, @NonNull final String timezone, final int formatClock, @NonNull final String formatDate, @NonNull final String formatTime, final int unitTemperature, final int unitPressure, final int unitMotion, final int screensaverDelay, final int theme) {
        id(id);
        timezone(timezone);
        formatClock(formatClock);
        formatDate(formatDate);
        formatTime(formatTime);
        unitTemperature(unitTemperature);
        unitPressure(unitPressure);
        unitMotion(unitMotion);
        screensaverDelay(screensaverDelay);
        theme(theme);
    }

    public Settings() {}

    @PrimaryKey
    private long id; //TODO: Should be userId for multiuser support. Every user has it's own settings record

    private String timezone;

    private int formatClock;

    private String formatDate;

    private String formatTime;

    private int unitTemperature;

    private int unitPressure;

    private int unitMotion;

    private int screensaverDelay;

    private int theme;

    @Override
    public long id() {
        return id;
    }

    @Override
    public void id(long id) {
        this.id = id;
    }

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

    public String formatDate() {
        return formatDate;
    }

    public void formatDate(final String formatDate) {
        this.formatDate = formatDate;
    }

    public String formatTime() {
        return formatTime;
    }

    public void formatTime(final String formatTime) {
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

    public int unitMotion() {
        return unitMotion;
    }

    public void unitMotion(final int unitMotion) {
        this.unitMotion = unitMotion;
    }

    public int screensaverDelay() {
        return screensaverDelay;
    }

    public void screensaverDelay(int screensaverDelay) {
        this.screensaverDelay = screensaverDelay;
    }

    public int theme() {
        return theme;
    }

    public void theme(final int theme) {
        this.theme = theme;
    }
}

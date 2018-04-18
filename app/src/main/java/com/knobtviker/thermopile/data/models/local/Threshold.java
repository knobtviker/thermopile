package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.BaseModel;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bojan on 15/06/2017.
 */

public class Threshold extends RealmObject implements BaseModel {

    @PrimaryKey
    private long id;

    @Index
    private int day;

    @Index
    private int startHour;

    @Index
    private int startMinute;

    private int endHour;

    private int endMinute;

    private int temperature;

    private String color;

    @Override
    public long id() {
        return id;
    }

    @Override
    public void id(final long id) {
        this.id = id;
    }

    public int day() {
        return day;
    }

    public void day(final int day) {
        this.day = day;
    }

    public int startHour() {
        return startHour;
    }

    public void startHour(final int startHour) {
        this.startHour = startHour;
    }

    public int startMinute() {
        return startMinute;
    }

    public void startMinute(final int startMinute) {
        this.startMinute = startMinute;
    }

    public int endHour() {
        return endHour;
    }

    public void endHour(final int endHour) {
        this.endHour = endHour;
    }

    public int endMinute() {
        return endMinute;
    }

    public void endMinute(final int endMinute) {
        this.endMinute = endMinute;
    }

    public int temperature() {
        return temperature;
    }

    public void temperature(final int temperature) {
        this.temperature = temperature;
    }

    public String color() {
        return color;
    }

    public void color(final String color) {
        this.color = color;
    }
}

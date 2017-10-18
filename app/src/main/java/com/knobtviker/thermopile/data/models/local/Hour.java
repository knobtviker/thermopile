package com.knobtviker.thermopile.data.models.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bojan on 27/07/2017.
 */

public class Hour extends RealmObject {

    public long id() {
        return id;
    }

    public void id(final long id) {
        this.id = id;
    }

    public int hour() {
        return hour;
    }

    public void hour(final int hour) {
        this.hour = hour;
    }

    public int startMinutes() {
        return startMinutes;
    }

    public void startMinutes(final int startMinutes) {
        this.startMinutes = startMinutes;
    }

    public int endMinutes() {
        return endMinutes;
    }

    public void endMinutes(final int endMinutes) {
        this.endMinutes = endMinutes;
    }

    public int color() {
        return color;
    }

    public void color(final int color) {
        this.color = color;
    }

    @PrimaryKey
    private long id;

    private int hour;

    private int startMinutes;

    private int endMinutes;

    private int color;

//    public Hour withColor(final int value) {
//        return toBuilder()
//            .color(value)
//            .build();
//    }
//
//    public Hour withStartMinutes(final int value) {
//        return toBuilder()
//            .startMinutes(value)
//            .build();
//    }
//
//    public Hour withEndMinutes(final int value) {
//        return toBuilder()
//            .endMinutes(value)
//            .build();
//    }
}

package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bojan on 15/06/2017.
 */

public class Mode extends RealmObject {

    public long id() {
        return id;
    }

    public void id(final long id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void name(@NonNull final String name) {
        this.name = name;
    }

    public Threshold threshold() {
        return threshold;
    }

    public void threshold(@NonNull final Threshold threshold) {
        this.threshold = threshold;
    }

    @PrimaryKey
    private long id;

    private String name;

    private Threshold threshold;
}

package com.knobtviker.thermopile.data.models.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.shared.SingleModel;

public class Motion extends SingleModel {

    public static Motion create(final long id, @NonNull final String vendor, @NonNull final String name, final float value, final long timestamp) {
        return new Motion(id, vendor, name, value, timestamp);
    }

    private Motion(final long id, @NonNull final String vendor, @NonNull final String name, final float value, final long timestamp) {
        this.id = id;
        this.vendor = vendor;
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }
}

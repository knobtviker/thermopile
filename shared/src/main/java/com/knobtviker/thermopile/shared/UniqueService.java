package com.knobtviker.thermopile.shared;

import android.app.Service;

import com.knobtviker.thermopile.shared.constants.Uid;

public abstract class UniqueService extends Service {

    private static int uid = Uid.INVALID;

    protected UniqueService(@Uid int id) {
        uid = id;
    }

    @Uid
    public static int uid() {
        return uid;
    }
}

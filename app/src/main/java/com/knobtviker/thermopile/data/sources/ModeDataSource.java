package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Mode;

import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface ModeDataSource {

    interface Local {

        RealmResults<Mode> load();

        void save(@NonNull final Mode item);
    }
}

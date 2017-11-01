package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;

import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface SettingsDataSource {

    interface Local {

        RealmResults<Settings> load();

        void saveTimezone(final long settingsId, @NonNull final String timezone);

        void saveClockMode(final long settingsId, final int clockMode);
    }
}

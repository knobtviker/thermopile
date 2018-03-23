package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface SettingsDataSource {

    interface Local {

        RealmResults<Settings> load(@NonNull final Realm realm);

        void saveTimezone(final long settingsId, @NonNull final String timezone);

        void saveClockMode(final long settingsId, final int clockMode);

        void saveFormatDate(final long settingsId, @NonNull final String item);

        void saveFormatTime(final long settingsId, @NonNull final String item);

        void saveTemperatureUnit(final long settingsId, final int unit);

        void savePressureUnit(final long settingsId, final int unit);

        void saveTheme(final long settingsId, final int value);
    }
}

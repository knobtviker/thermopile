package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.SettingsTableEntity;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by bojan on 18/07/2017.
 */

public interface SettingsDataSource {

    interface Local {

        Observable<SettingsTableEntity> load();

        Single<SettingsTableEntity> save(@NonNull final SettingsTableEntity item);
    }
}

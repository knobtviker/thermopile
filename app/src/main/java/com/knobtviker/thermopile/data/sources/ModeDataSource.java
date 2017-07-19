package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.ModeTableEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by bojan on 18/07/2017.
 */

public interface ModeDataSource {

    interface Local {

        Single<List<ModeTableEntity>> load();

        Single<ModeTableEntity> save(@NonNull final ModeTableEntity item);
    }
}

package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.ThresholdTableEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by bojan on 18/07/2017.
 */

public interface ThresholdDataSource {

    interface Local {

        Single<List<ThresholdTableEntity>> load();

        Single<ThresholdTableEntity> save(@NonNull final ThresholdTableEntity item);

        Single<List<ThresholdTableEntity>> loadByDay(final int day);
    }
}

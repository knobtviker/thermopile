package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.AtmosphereTableEntity;
import com.knobtviker.thermopile.data.models.raw.Triplet;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by bojan on 18/07/2017.
 */

public interface AtmosphereDataSource {

    interface Raw {

        Single<Triplet<Float, Float, Float>> read();

        Single<Float> readLuminosity();
    }

    interface Local {

        Single<List<AtmosphereTableEntity>> load();

        Single<AtmosphereTableEntity> save(@NonNull final AtmosphereTableEntity item);

        Observable<AtmosphereTableEntity> last();
    }
}

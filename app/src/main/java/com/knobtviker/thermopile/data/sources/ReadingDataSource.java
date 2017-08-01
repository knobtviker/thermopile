package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.ReadingTableEntity;
import com.knobtviker.thermopile.data.models.raw.Triplet;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by bojan on 18/07/2017.
 */

public interface ReadingDataSource {

    interface Raw {

        Single<Triplet<Float, Float, Float>> read();
    }

    interface Local {

        Single<List<ReadingTableEntity>> load();

        Single<ReadingTableEntity> save(@NonNull final ReadingTableEntity item);

        Observable<ReadingTableEntity> last();
    }
}

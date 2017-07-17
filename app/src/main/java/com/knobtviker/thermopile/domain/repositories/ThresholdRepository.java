package com.knobtviker.thermopile.domain.repositories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class ThresholdRepository extends BaseRepository {

    private static Optional<ThresholdRepository> INSTANCE = Optional.empty();

//    private final ThresholdLocalDataSource thresholdLocalDataSource;
//    private final ThresholdConverter thresholdConverter;

    public static ThresholdRepository getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ThresholdRepository(context));
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
//            ThresholdLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private ThresholdRepository(@NonNull final Context context) {
//        this.thresholdLocalDataSource = ThresholdLocalDataSource.getInstance(context);
//        this.thresholdConverter = new ThresholdConverter();
    }

    public Observable<Threshold> save(Threshold threshold) {
//        return thresholdLocalDataSource
//            .save(threshold)
//            ;
        return Observable.empty();
    }
}

package com.knobtviker.thermopile.domain.repositories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.ThresholdConverter;
import com.knobtviker.thermopile.data.models.local.ThresholdTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class ThresholdRepository extends BaseRepository {

    private static Optional<ThresholdRepository> INSTANCE = Optional.empty();

    private final ThresholdLocalDataSource thresholdLocalDataSource;
    private final ThresholdConverter thresholdConverter;

    public static ThresholdRepository getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ThresholdRepository(context));
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            ThresholdLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private ThresholdRepository(@NonNull final Context context) {
        this.thresholdLocalDataSource = ThresholdLocalDataSource.getInstance(context);
        this.thresholdConverter = new ThresholdConverter();
    }

    public Observable<ImmutableList<Threshold>> load() {
        return thresholdLocalDataSource
            .load()
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(ImmutableList::copyOf)
            .map(thresholdConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Threshold> save(@NonNull final ThresholdTableEntity entity) {
        return thresholdLocalDataSource
            .save(entity)
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(thresholdConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<ThresholdTableEntity> convertToLocal(@NonNull final Threshold threshold) {
        return Observable.defer(() -> Observable.just(thresholdConverter.presentationToLocal(threshold)));
    }
}

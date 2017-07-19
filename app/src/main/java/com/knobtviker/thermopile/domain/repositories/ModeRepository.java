package com.knobtviker.thermopile.domain.repositories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.ModeConverter;
import com.knobtviker.thermopile.data.models.local.ModeTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Mode;
import com.knobtviker.thermopile.data.sources.local.ModeLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class ModeRepository extends BaseRepository {

    private static Optional<ModeRepository> INSTANCE = Optional.empty();

    private final ModeLocalDataSource modeLocalDataSource;
    private final ModeConverter modeConverter;

    public static ModeRepository getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ModeRepository(context));
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            ModeLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private ModeRepository(@NonNull final Context context) {
        this.modeLocalDataSource = ModeLocalDataSource.getInstance(context);
        this.modeConverter = new ModeConverter();
    }

    public Observable<ImmutableList<Mode>> load() {
        return modeLocalDataSource
            .load()
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(ImmutableList::copyOf)
            .map(modeConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Mode> save(@NonNull final ModeTableEntity entity) {
        return modeLocalDataSource
            .save(entity)
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(modeConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<ModeTableEntity> convertToLocal(@NonNull final Mode mode) {
        return Observable.defer(() -> Observable.just(modeConverter.presentationToLocal(mode)));
    }
}

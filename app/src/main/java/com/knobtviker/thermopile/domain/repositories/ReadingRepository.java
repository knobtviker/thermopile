package com.knobtviker.thermopile.domain.repositories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.ReadingConverter;
import com.knobtviker.thermopile.data.models.local.ReadingTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Reading;
import com.knobtviker.thermopile.data.sources.local.ReadingLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.ReadingRawDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class ReadingRepository extends BaseRepository {

    private static Optional<ReadingRepository> INSTANCE = Optional.empty();

    private final ReadingRawDataSource readingRawDataSource;
    private final ReadingLocalDataSource readingLocalDataSource;
    private final ReadingConverter readingConverter;

    public static ReadingRepository getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ReadingRepository(context));
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            ReadingRawDataSource.destroyInstance();
            ReadingLocalDataSource.destroyInstance();
            INSTANCE = Optional.empty();
        }
    }

    private ReadingRepository(@NonNull final Context context) {
        this.readingLocalDataSource = ReadingLocalDataSource.getInstance(context);
        this.readingRawDataSource = ReadingRawDataSource.getInstance();
        this.readingConverter = new ReadingConverter();
    }

    public Observable<ReadingTableEntity> read() {
        return readingRawDataSource
            .read()
            .toObservable()
            .subscribeOn(schedulerProvider.sensors())
            .map(readingConverter::rawToLocal)
            .observeOn(schedulerProvider.io());
    }

    public Observable<ImmutableList<Reading>> load() {
        return readingLocalDataSource
            .load()
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(ImmutableList::copyOf)
            .map(readingConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }

    public Observable<Reading> save(@NonNull final ReadingTableEntity entity) {
        return readingLocalDataSource
            .save(entity)
            .toObservable()
            .subscribeOn(schedulerProvider.io())
            .map(readingConverter::localToPresentation)
            .observeOn(schedulerProvider.ui());
    }
}

package com.knobtviker.thermopile.data.converters;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.implementation.LocalToPresentation;
import com.knobtviker.thermopile.data.converters.implementation.RawToLocal;
import com.knobtviker.thermopile.data.models.local.ReadingTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Reading;
import com.knobtviker.thermopile.data.models.raw.Triplet;

import org.joda.time.DateTimeUtils;

import java.util.stream.Collectors;

/**
 * Created by bojan on 19/07/2017.
 */

public class ReadingConverter implements RawToLocal<Triplet<Float, Float, Float>, ReadingTableEntity>, LocalToPresentation<ReadingTableEntity, Reading> {

    @Override
    public ImmutableList<ReadingTableEntity> rawToLocal(@NonNull ImmutableList<Triplet<Float, Float, Float>> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::rawToLocal)
                .collect(Collectors.toList())
        );
    }

    @Override
    public ReadingTableEntity rawToLocal(@NonNull Triplet<Float, Float, Float> item) {
        final ReadingTableEntity entity = new ReadingTableEntity();
        entity.timestamp(DateTimeUtils.currentTimeMillis());
        entity.temperature(item.first());
        entity.humidity(item.second());
        entity.pressure(item.third());
        return entity;
    }

    @Override
    public ImmutableList<Reading> localToPresentation(@NonNull ImmutableList<ReadingTableEntity> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::localToPresentation)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Reading localToPresentation(@NonNull ReadingTableEntity item) {
        return Reading.builder()
            .id(item.id())
            .timestamp(item.timestamp())
            .temperature(item.temperature())
            .humidity(item.humidity())
            .pressure(item.pressure())
            .build();
    }
}

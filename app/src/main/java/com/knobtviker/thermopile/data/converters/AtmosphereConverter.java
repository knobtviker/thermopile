package com.knobtviker.thermopile.data.converters;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.implementation.LocalToPresentation;
import com.knobtviker.thermopile.data.converters.implementation.RawToLocal;
import com.knobtviker.thermopile.data.models.local.AtmosphereTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.data.models.raw.Triplet;

import org.joda.time.DateTimeUtils;

import java.util.stream.Collectors;

/**
 * Created by bojan on 19/07/2017.
 */

public class AtmosphereConverter implements RawToLocal<Triplet<Float, Float, Float>, AtmosphereTableEntity>, LocalToPresentation<AtmosphereTableEntity, Atmosphere> {

    @Override
    public ImmutableList<AtmosphereTableEntity> rawToLocal(@NonNull ImmutableList<Triplet<Float, Float, Float>> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::rawToLocal)
                .collect(Collectors.toList())
        );
    }

    @Override
    public AtmosphereTableEntity rawToLocal(@NonNull Triplet<Float, Float, Float> item) {
        final AtmosphereTableEntity entity = new AtmosphereTableEntity();
        entity.timestamp(DateTimeUtils.currentTimeMillis());
        entity.temperature(item.first());
        entity.humidity(item.second());
        entity.pressure(item.third());
        return entity;
    }

    @Override
    public ImmutableList<Atmosphere> localToPresentation(@NonNull ImmutableList<AtmosphereTableEntity> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::localToPresentation)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Atmosphere localToPresentation(@NonNull AtmosphereTableEntity item) {
        return Atmosphere.builder()
            .id(item.id())
            .timestamp(item.timestamp())
            .temperature(item.temperature())
            .humidity(item.humidity())
            .pressure(item.pressure())
            .build();
    }
}

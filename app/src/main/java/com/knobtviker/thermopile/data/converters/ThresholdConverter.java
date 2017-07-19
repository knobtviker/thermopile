package com.knobtviker.thermopile.data.converters;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.implementation.LocalToPresentation;
import com.knobtviker.thermopile.data.converters.implementation.PresentationToLocal;
import com.knobtviker.thermopile.data.models.local.ThresholdTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Threshold;

import java.util.stream.Collectors;

/**
 * Created by bojan on 18/07/2017.
 */

public class ThresholdConverter implements LocalToPresentation<ThresholdTableEntity, Threshold>, PresentationToLocal<Threshold, ThresholdTableEntity> {

    @Override
    public ImmutableList<Threshold> localToPresentation(@NonNull ImmutableList<ThresholdTableEntity> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::localToPresentation)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Threshold localToPresentation(@NonNull ThresholdTableEntity item) {
        return Threshold.builder()
            .id(item.id())
            .day(item.day())
            .startHour(item.startHour())
            .startMinute(item.startMinute())
            .endHour(item.endHour())
            .endMinute(item.endMinute())
            .temperature(item.temperature())
            .color(item.color())
            .build();
    }

    @Override
    public ImmutableList<ThresholdTableEntity> presentationToLocal(@NonNull ImmutableList<Threshold> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::presentationToLocal)
                .collect(Collectors.toList())
        );
    }

    @Override
    public ThresholdTableEntity presentationToLocal(@NonNull Threshold item) {
        final ThresholdTableEntity entity = new ThresholdTableEntity();
        entity.day(item.day());
        entity.startHour(item.startHour());
        entity.startMinute(item.startMinute());
        entity.endHour(item.endHour());
        entity.endMinute(item.endMinute());
        entity.temperature(item.temperature());
        entity.color(item.color());
        return entity;
    }
}

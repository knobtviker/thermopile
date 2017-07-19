package com.knobtviker.thermopile.data.converters;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.implementation.LocalToPresentation;
import com.knobtviker.thermopile.data.converters.implementation.PresentationToLocal;
import com.knobtviker.thermopile.data.models.local.ModeTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Mode;

import java.util.stream.Collectors;

/**
 * Created by bojan on 18/07/2017.
 */

public class ModeConverter implements LocalToPresentation<ModeTableEntity, Mode>, PresentationToLocal<Mode, ModeTableEntity> {

    private final ThresholdConverter thresholdConverter;

    public ModeConverter() {
        this.thresholdConverter = new ThresholdConverter();
    }

    @Override
    public ImmutableList<Mode> localToPresentation(@NonNull ImmutableList<ModeTableEntity> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::localToPresentation)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Mode localToPresentation(@NonNull ModeTableEntity item) {
        return Mode.builder()
            .id(item.id())
            .name(item.name())
            .threshold(thresholdConverter.localToPresentation(item.threshold()))
            .build();
    }

    @Override
    public ImmutableList<ModeTableEntity> presentationToLocal(@NonNull ImmutableList<Mode> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::presentationToLocal)
                .collect(Collectors.toList())
        );
    }

    @Override
    public ModeTableEntity presentationToLocal(@NonNull Mode item) {
        final ModeTableEntity entity = new ModeTableEntity();
        entity.name(item.name());
        entity.threshold(thresholdConverter.presentationToLocal(item.threshold()));
        return entity;
    }
}

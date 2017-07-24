package com.knobtviker.thermopile.data.converters;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.implementation.LocalToPresentation;
import com.knobtviker.thermopile.data.converters.implementation.PresentationToLocal;
import com.knobtviker.thermopile.data.models.local.SettingsTableEntity;
import com.knobtviker.thermopile.data.models.presentation.Settings;

import java.util.stream.Collectors;

/**
 * Created by bojan on 24/07/2017.
 */

public class SettingsConverter implements LocalToPresentation<SettingsTableEntity, Settings>, PresentationToLocal<Settings, SettingsTableEntity> {
    @Override
    public ImmutableList<Settings> localToPresentation(@NonNull ImmutableList<SettingsTableEntity> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::localToPresentation)
                .collect(Collectors.toList())
        );
    }

    //TODO: Make this return real values instead of empty fake for test
    @Override
    public Settings localToPresentation(@NonNull SettingsTableEntity item) {
        return Settings.builder()
            .id(item.id())
            .timezone(item.timezone())
            .formatClock(item.formatClock())
            .formatDate(item.formatDate())
            .formatTime(item.formatTime())
            .unitTemperature(item.unitTemperature())
            .unitPressure(item.unitPressure())
            .build();
    }

    @Override
    public ImmutableList<SettingsTableEntity> presentationToLocal(@NonNull ImmutableList<Settings> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::presentationToLocal)
                .collect(Collectors.toList())
        );
    }

    @Override
    public SettingsTableEntity presentationToLocal(@NonNull Settings item) {
        final SettingsTableEntity entity = new SettingsTableEntity();
        entity.id(item.id());
        entity.timezone(item.timezone());
        entity.formatClock(item.formatClock());
        entity.formatDate(item.formatDate());
        entity.formatTime(item.formatTime());
        entity.unitTemperature(item.unitTemperature());
        entity.unitPressure(item.unitPressure());
        return entity;
    }
}

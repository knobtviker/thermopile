package com.knobtviker.thermopile.data.converters;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.converters.implementation.RawToLocal;
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.raw.Triplet;

import org.joda.time.DateTimeUtils;

import java.util.stream.Collectors;

/**
 * Created by bojan on 19/07/2017.
 */

public class AtmosphereConverter implements RawToLocal<Triplet<Float, Float, Float>, Atmosphere> {

    @Override
    public ImmutableList<Atmosphere> rawToLocal(@NonNull ImmutableList<Triplet<Float, Float, Float>> items) {
        return ImmutableList.copyOf(
            items
                .stream()
                .map(this::rawToLocal)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Atmosphere rawToLocal(@NonNull Triplet<Float, Float, Float> item) {
        final Atmosphere realmObject = new Atmosphere();

        realmObject.timestamp(DateTimeUtils.currentTimeMillis());
        realmObject.temperature(item.first());
        realmObject.humidity(item.second());
        realmObject.pressure(item.third());

        return realmObject;
    }
}

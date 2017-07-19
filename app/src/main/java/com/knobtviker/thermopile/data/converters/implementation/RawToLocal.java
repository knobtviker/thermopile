package com.knobtviker.thermopile.data.converters.implementation;

/**
 * Created by bojan on 19/07/2017.
 */

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * Created by Bojan Komljenovic on 17.2.2017. at 12:58.
 *
 * Parameter R is raw model class.
 * Parameter L is local model class.
 * <p>
 * List of models and single model support.
 */
public interface RawToLocal<R, L> {

    ImmutableList<L> rawToLocal(@NonNull final ImmutableList<R> items);

    L rawToLocal(@NonNull final R item);
}

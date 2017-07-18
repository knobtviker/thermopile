package com.knobtviker.thermopile.data.converters.implementation;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * Created by Bojan Komljenovic on 17.2.2017. at 12:58.
 *
 * Parameter L is local model class.
 * Parameter P is presentation model class.
 * <p>
 * List of models and single model support.
 */
public interface LocalToPresentation<L, P> {

    ImmutableList<P> localToPresentation(@NonNull final ImmutableList<L> items);

    P localToPresentation(@NonNull final L item);
}

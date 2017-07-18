package com.knobtviker.thermopile.data.converters.implementation;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * Created by Bojan Komljenovic on 17.2.2017. at 12:58.
 *
 * Parameter P is presentation model class.
 * Parameter L is local model class.
 * <p>
 * List of models and single model support.
 */
public interface PresentationToLocal<P, L> {

    ImmutableList<L> presentationToLocal(@NonNull final ImmutableList<P> items);

    L presentationToLocal(@NonNull final P item);
}

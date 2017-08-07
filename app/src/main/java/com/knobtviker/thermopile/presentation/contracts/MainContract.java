package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.data.models.presentation.Settings;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface MainContract {

    interface View extends BaseView {

        void onClockTick();

        void onData(@NonNull final Atmosphere data);

        void onThresholds(@NonNull final ImmutableList<Threshold> thresholds);

        void onSettings(@NonNull final Settings settings);
    }

    interface Presenter extends BasePresenter {

        void startClock();

        void data();

        void thresholdsForToday(final int day);

        void settings();
    }
}

package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ScheduleContract {

    interface View extends BaseView {

        void onThresholds(@NonNull final ImmutableList<Threshold> thresholds);

        void onSaved(@NonNull final Threshold threshold);
    }

    interface Presenter extends BasePresenter {

        void thresholds();

        void save(@NonNull final Threshold threshold);
    }
}

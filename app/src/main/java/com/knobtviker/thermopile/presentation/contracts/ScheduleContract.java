package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ScheduleContract {

    interface View extends BaseView {

        void onThresholds(@NonNull final RealmResults<Threshold> thresholds);
    }

    interface Presenter extends BasePresenter {

        void thresholds();
    }
}

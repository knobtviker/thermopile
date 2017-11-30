package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import io.realm.Realm;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ThresholdContract {

    interface View extends BaseView {

        void onThreshold(@NonNull final Threshold threshold);

        void onSaved();
    }

    interface Presenter extends BasePresenter {

        void loadById(@NonNull final Realm realm, final long thresholdId);

        void save(@NonNull final Threshold threshold);
    }
}

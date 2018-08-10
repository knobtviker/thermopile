package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ThresholdContract {

    interface View extends BaseView {

        void onSettingsChanged(@NonNull final Settings settings);

        void onThreshold(@NonNull final Threshold threshold);

        void onSaved();
    }

    interface Presenter extends BasePresenter {

        void settings();

        void loadById(final long thresholdId);

        void save(@NonNull final Threshold threshold);
    }
}

package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ScheduleContract {

    interface View extends BaseView {

        void onSettingsChanged(@NonNull final Settings settings);

        void onThresholds(@NonNull final List<Threshold> thresholds);
    }

    interface Presenter extends BasePresenter {

        void settings();

        void thresholds();

        void removeThresholdById(final long id);
    }
}

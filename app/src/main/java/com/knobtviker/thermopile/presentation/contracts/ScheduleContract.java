package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.presentation.ThresholdChip;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ScheduleContract {

    interface View extends BaseView {

        void setWeekdayNames(final boolean isShortName);

        void onThresholds(@NonNull final List<ThresholdChip> thresholds);
    }

    interface Presenter extends BasePresenter {

        void load();

        void removeThresholdById(final long id);
    }
}

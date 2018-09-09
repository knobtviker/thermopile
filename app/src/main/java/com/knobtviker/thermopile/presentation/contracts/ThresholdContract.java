package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ThresholdContract {

    interface View extends BaseView {

        void updateDialogStartTime(int startHour, int startMinute);

        void onTemperatureUnitChanged(@StringRes int resId, final float minimum, final float maximum);

        void onClockAndTimeFormatChanged(final boolean is24hMode, @NonNull final String formattedTimeNow);

        void onStartTimeChanged(@NonNull final String formattedTime);

        void onEndTimeChanged(@NonNull final String formattedTime);

        void onThreshold(@NonNull final Threshold threshold);

        void onSaved();
    }

    interface Presenter extends BasePresenter {

        void setStartTime(final int day, final int hour, final int minute);

        void setEndTime(final int day, final int hour, final int minute);

        void loadById(final long thresholdId);

        void createNew(final int day, final int minute, final int maxWidth);

        void save(@NonNull final Threshold threshold);
    }
}

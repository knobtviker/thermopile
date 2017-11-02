package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface SettingsContract {

    interface View extends BaseView {

        void onLoad(@NonNull final Settings settings);
    }

    interface Presenter extends BasePresenter {

        void load();

        void saveTimezone(final long settingsId, @NonNull final String timezone);

        void saveClockMode(final long settingsId, final int clockMode);

        void saveFormatDate(final long settingsId, @NonNull final String item);

        void saveFormatTime(final long settingsId, @NonNull final String item);

        void saveTemperatureUnit(final long settingsId, final int unit);

        void savePressureUnit(final long settingsId, final int unit);
    }
}

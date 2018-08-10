package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface LocaleContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {

        void saveTimezone(final long settingsId, @NonNull final String timezone);

        void saveClockMode(final long settingsId, final int clockMode);
    }
}

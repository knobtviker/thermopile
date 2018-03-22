package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface FormatContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {

        void saveFormatDate(final long settingsId, @NonNull final String item);

        void saveFormatTime(final long settingsId, @NonNull final String item);

    }
}

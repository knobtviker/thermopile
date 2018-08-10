package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface FormatsContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {

        void saveFormatDate(final long settingsId, @NonNull final String item);

        void saveFormatTime(final long settingsId, @NonNull final String item);

    }
}

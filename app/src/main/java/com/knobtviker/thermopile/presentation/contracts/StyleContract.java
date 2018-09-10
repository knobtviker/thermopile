package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface StyleContract {

    interface View extends BaseView {

        void setTheme(int theme);

        void setScreenSaverTimeout(int index);
    }

    interface Presenter extends BasePresenter {

        void load(@NonNull final List<Integer> screenSaverTimeouts);

        void saveTheme(final int value);

        void saveScreensaverTimeout(final int value);
    }
}

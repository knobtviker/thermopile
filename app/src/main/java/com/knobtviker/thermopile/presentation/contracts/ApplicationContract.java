package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void onTick();

        void showScreensaver();
    }

    interface Presenter extends BasePresenter {

        void tick();

        void createScreensaver();

        void destroyScreensaver();

        void initScreen(final int density, final int rotation, final long timeout);

        void brightness(final int brightness);

        void saveAtmosphere(@NonNull final Atmosphere atmosphere);
    }
}

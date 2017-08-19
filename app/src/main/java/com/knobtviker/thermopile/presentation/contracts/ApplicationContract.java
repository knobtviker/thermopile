package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void onClockTick();

        void onAtmosphereData(@NonNull final Atmosphere data);

        void onLuminosityData(final float luminosity);
    }

    interface Presenter extends BasePresenter {

        void startClock();

        void atmosphereData();

        void luminosityData();
    }
}

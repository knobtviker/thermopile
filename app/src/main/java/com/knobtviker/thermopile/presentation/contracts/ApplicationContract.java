package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void showScreensaver();

    }

    interface Presenter extends BasePresenter {

        void createScreensaver();

        void destroyScreensaver();

        void saveTemperatures(@NonNull final List<Temperature> items);

        void saveHumidities(@NonNull final List<Humidity> items);

        void savePressures(@NonNull final List<Pressure> items);

        void initScreen(final int density, final int rotation, final long timeout);

        void brightness(final int brightness);
    }
}

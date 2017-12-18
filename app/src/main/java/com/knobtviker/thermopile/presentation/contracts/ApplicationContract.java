package com.knobtviker.thermopile.presentation.contracts;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

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

        void saveData(final float temperature, final float humidity, final float pressure,
                      final int temperatureAccuracy, final int humidityAccuracy, final int pressureAccuracy);

        void initScreen(final int density, final int rotation, final long timeout);

        void brightness(final int brightness);
    }
}

package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface SensorsContract {

    interface View extends BaseView {

        void onSensors(@NonNull final List<PeripheralDevice> sensors);
    }

    interface Presenter extends BasePresenter {

        void sensors();

        void sensorEnabled(final long id, final int type, final boolean isChecked);
    }
}

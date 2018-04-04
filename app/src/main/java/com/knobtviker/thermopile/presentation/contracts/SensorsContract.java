package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public interface SensorsContract {

    interface View extends BaseView {

        void onSensors(@NonNull final RealmResults<PeripheralDevice> sensors);
    }

    interface Presenter extends BasePresenter {

        void sensors(@NonNull final Realm realm);
    }
}

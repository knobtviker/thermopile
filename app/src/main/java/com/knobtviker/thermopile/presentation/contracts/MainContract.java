package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public interface MainContract {

    interface View extends BaseView {

        void onTemperatureChanged(@NonNull final Temperature data);

        void onHumidityChanged(@NonNull final Humidity data);

        void onPressueChanged(@NonNull final Pressure data);

        void onSettingsChanged(@NonNull final Settings settings);

        void onThresholdsChanged(@NonNull final RealmResults<Threshold> thresholds);
    }

    interface Presenter extends BasePresenter {

        void temperature(@NonNull final Realm realm);

        void humidity(@NonNull final Realm realm);

        void pressure(@NonNull final Realm realm);

        void settings(@NonNull final Realm realm);

        void thresholdsForToday(@NonNull final Realm realm, final int day);
    }
}

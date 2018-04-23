package com.knobtviker.thermopile.presentation.contracts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void onSettings(@NonNull final Settings settings);

        void showScreensaver();
    }

    interface Presenter extends BasePresenter {

        void observeSensors(@NonNull final Context context);

        void createScreensaver();

        void destroyScreensaver();

        void initScreen(final int density, final int rotation);

        void brightness(final int brightness);

        void settings(@NonNull final Realm realm);

        void peripherals(@NonNull final Realm realm);

        ImmutableList<I2CDevice> i2cDevices();

        RealmResults<PeripheralDevice> defaultSensors(@NonNull final Realm realm);

        void saveDefaultSensors(@NonNull final List<PeripheralDevice> foundSensors);
    }
}

package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.fragments.WirelessFragment;
import com.knobtviker.thermopile.presentation.presenters.WirelessPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class WirelessFragmentModule {

    @Provides
    WirelessContract.Presenter providePresenter(@NonNull final Context context, @NonNull final WirelessContract.View view) {
        return new WirelessPresenter(context, view);
    }

    @Provides
    WirelessContract.View provideView(@NonNull final WirelessFragment fragment) {
        return fragment;
    }
}

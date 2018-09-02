package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.fragments.WirelessFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class WirelessFragmentModule {

    @Provides
    WirelessContract.View provideView(@NonNull final WirelessFragment fragment) {
        return fragment;
    }
}

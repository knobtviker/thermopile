package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.NetworkFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkFragmentModule {

    @Provides
    NetworkContract.View provideView(@NonNull final NetworkFragment fragment) {
        return fragment;
    }
}

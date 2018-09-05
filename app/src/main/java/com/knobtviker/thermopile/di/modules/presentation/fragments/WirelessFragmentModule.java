package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.NetworkRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.fragments.WirelessFragment;
import com.knobtviker.thermopile.presentation.presenters.WirelessPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class WirelessFragmentModule {

    @Provides
    WirelessContract.Presenter providePresenter(
        @NonNull final WirelessContract.View view,
        @NonNull final NetworkRepository networkRepository,
        @NonNull final Schedulers schedulers
        ) {
        return new WirelessPresenter(view, networkRepository, schedulers);
    }

    @Provides
    WirelessContract.View provideView(@NonNull final WirelessFragment fragment) {
        return fragment;
    }
}

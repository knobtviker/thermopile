package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.NetworkRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.NetworkFragment;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkFragmentModule {

    @Provides
    long provideDefaultSettings() {
        return -1L;
    }

    @Provides
    NetworkContract.Presenter providePresenter(
        @NonNull final NetworkContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final NetworkRepository networkRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new NetworkPresenter(view, atmosphereRepository, networkRepository, schedulers);
    }

    @Provides
    NetworkContract.View provideView(@NonNull final NetworkFragment fragment) {
        return fragment;
    }
}

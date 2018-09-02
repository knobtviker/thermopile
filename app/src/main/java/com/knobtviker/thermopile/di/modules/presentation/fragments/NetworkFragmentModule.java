package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.NetworkFragment;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkFragmentModule {

    @Provides
    NetworkContract.Presenter providePresenter(@NonNull final Context context, @NonNull final NetworkContract.View view) {
        return new NetworkPresenter(context, view);
    }

    @Provides
    NetworkContract.View provideView(@NonNull final NetworkFragment fragment) {
        return fragment;
    }
}

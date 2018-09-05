package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.BlankContract;
import com.knobtviker.thermopile.presentation.fragments.BlankFragment;
import com.knobtviker.thermopile.presentation.presenters.BlankPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class BlankFragmentModule {

    @Provides
    BlankContract.Presenter providePresenter(
        @NonNull final BlankContract.View view,
        @NonNull final Schedulers schedulers
    ) {
        return new BlankPresenter(view, schedulers);
    }

    @Provides
    BlankContract.View provideView(@NonNull final BlankFragment fragment) {
        return fragment;
    }
}

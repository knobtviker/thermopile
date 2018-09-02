package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;
import com.knobtviker.thermopile.presentation.presenters.SettingsPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsFragmentModule {

    @Provides
    SettingsContract.Presenter providePresenter(@NonNull final SettingsContract.View view) {
        return new SettingsPresenter(view);
    }

    @Provides
    SettingsContract.View provideView(@NonNull final SettingsFragment fragment) {
        return fragment;
    }
}

package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsFragmentModule {

    @Provides
    BaseView provideView(@NonNull final SettingsFragment fragment) {
        return fragment;
    }
}

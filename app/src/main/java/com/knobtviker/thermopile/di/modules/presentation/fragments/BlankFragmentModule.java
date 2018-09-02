package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.fragments.BlankFragment;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

import dagger.Module;
import dagger.Provides;

@Module
public class BlankFragmentModule {

    @Provides
    BaseView provideView(@NonNull final BlankFragment fragment) {
        return fragment;
    }
}

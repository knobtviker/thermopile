package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultScreenSaverTimeout;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTheme;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.fragments.StyleFragment;
import com.knobtviker.thermopile.presentation.presenters.StylePresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ScreensaverTimeout;

import dagger.Module;
import dagger.Provides;

@Module
public class StyleFragmentModule {

    @Provides
    @DefaultTheme
    int provideDefaultTheme() {
        return Default.THEME;
    }

    @Provides
    @DefaultScreenSaverTimeout
    int provideDefaultScreenSaverTimeout() {
        return ScreensaverTimeout._1MIN;
    }

    @Provides
    StyleContract.Presenter providePresenter(@NonNull final StyleContract.View view) {
        return new StylePresenter(view);
    }

    @Provides
    StyleContract.View provideView(@NonNull final StyleFragment fragment) {
        return fragment;
    }
}

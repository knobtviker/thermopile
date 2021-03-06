package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.fragments.StyleFragment;
import com.knobtviker.thermopile.presentation.presenters.StylePresenter;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ScreensaverTimeout;
import com.knobtviker.thermopile.presentation.views.adapters.TimeoutAdapter;

import java.util.Arrays;

import dagger.Module;
import dagger.Provides;

@Module
public class StyleFragmentModule {

    @Provides
    TimeoutAdapter provideTimeoutAdapter(@NonNull final Context context) {
        return new TimeoutAdapter(
            context,
            Arrays.asList(
                ScreensaverTimeout._15S,
                ScreensaverTimeout._30S,
                ScreensaverTimeout._1MIN,
                ScreensaverTimeout._2MIN,
                ScreensaverTimeout._5MIN,
                ScreensaverTimeout._10MIN
            )
        );
    }

    @Provides
    StyleContract.Presenter providePresenter(
        @NonNull final StyleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers,
        @NonNull final SharedPreferences preferences
    ) {
        return new StylePresenter(view, settingsRepository, schedulers, preferences);
    }

    @Provides
    StyleContract.View provideView(@NonNull final StyleFragment fragment) {
        return fragment;
    }
}

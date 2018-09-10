package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.fragments.ThresholdFragment;
import com.knobtviker.thermopile.presentation.presenters.ThresholdPresenter;
import com.knobtviker.thermopile.presentation.views.adapters.ColorAdapter;

import dagger.Module;
import dagger.Provides;

@Module
public class ThresholdFragmentModule {

    @Provides
    ColorAdapter provideColorAdapter(@NonNull final Context context) {
        return new ColorAdapter(context);
    }

    @Provides
    ThresholdContract.Presenter providePresenter(
        @NonNull final ThresholdContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
        ) {
        return new ThresholdPresenter(view, settingsRepository, thresholdRepository, schedulers);
    }

    @Provides
    ThresholdContract.View provideView(@NonNull final ThresholdFragment fragment) {
        return fragment;
    }
}

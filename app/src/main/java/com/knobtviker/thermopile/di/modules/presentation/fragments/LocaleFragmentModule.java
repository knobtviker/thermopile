package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.adapters.FormatDateAdapter;
import com.knobtviker.thermopile.di.qualifiers.presentation.adapters.FormatTimeAdapter;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.fragments.LocaleFragment;
import com.knobtviker.thermopile.presentation.presenters.LocalePresenter;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.views.adapters.FormatAdapter;
import com.knobtviker.thermopile.presentation.views.adapters.TimezoneAdapter;

import java.util.Arrays;

import dagger.Module;
import dagger.Provides;

@Module
public class LocaleFragmentModule {

    @Provides
    TimezoneAdapter provideTimezoneAdapter(@NonNull final Context context) {
        return new TimezoneAdapter(context, DateTimeKit.zones());
    }

    @Provides
    @FormatDateAdapter
    FormatAdapter provideFormatDateAdapter(@NonNull final Context context) {
        return new FormatAdapter(
            context,
            Arrays.asList(
                FormatDate.EEEE_DD_MM_YYYY,
                FormatDate.EE_DD_MM_YYYY,
                FormatDate.DD_MM_YYYY,
                FormatDate.EEEE_MM_DD_YYYY,
                FormatDate.EE_MM_DD_YYYY,
                FormatDate.MM_DD_YYYY
            )
        );
    }

    @Provides
    @FormatTimeAdapter
    FormatAdapter provideFormatTimeAdapter(@NonNull final Context context) {
        return new FormatAdapter(
            context,
            Arrays.asList(
                FormatTime.HH_MM,
                FormatTime.H_M,
                FormatTime.KK_MM_A,
                FormatTime.K_M_A
            )
        );
    }

    @Provides
    LocaleContract.Presenter providePresenter(
        @NonNull final LocaleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new LocalePresenter(view, settingsRepository, schedulers);
    }

    @Provides
    LocaleContract.View provideView(@NonNull final LocaleFragment fragment) {
        return fragment;
    }
}

package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.adapters.FormatDateAdapter;
import com.knobtviker.thermopile.di.qualifiers.presentation.adapters.FormatTimeAdapter;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultClockMode;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.fragments.LocaleFragment;
import com.knobtviker.thermopile.presentation.presenters.LocalePresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
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
    long provideDefaultSettings() {
        return -1L;
    }

    @Provides
    String provideDefaultZone() {
        return Default.TIMEZONE;
    }

    @Provides
    @DefaultClockMode
    int provideDefaultClockMode() {
        return ClockMode._24H;
    }

    @Provides
    @DefaultFormatDate
    String provideDefaultFormatDate() {
        return FormatDate.EEEE_DD_MM_YYYY;
    }

    @Provides
    @DefaultFormatTime
    String provideDefaultFormatTime() {
        return FormatTime.HH_MM;
    }

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
    LocaleContract.Presenter providePresenter(@NonNull final LocaleContract.View view) {
        return new LocalePresenter(view);
    }

    @Provides
    LocaleContract.View provideView(@NonNull final LocaleFragment fragment) {
        return fragment;
    }
}

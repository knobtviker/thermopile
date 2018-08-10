package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class LocalePresenter extends AbstractPresenter implements LocaleContract.Presenter {

    private final LocaleContract.View view;

    private final SettingsRepository settingsRepository;

    public LocalePresenter(@NonNull final LocaleContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void saveTimezone(long settingsId, @NonNull String timezone) {
        compositeDisposable.add(
            settingsRepository
                .saveTimezone(settingsId, timezone)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveClockMode(long settingsId, int clockMode) {
        compositeDisposable.add(
            settingsRepository
                .saveClockMode(settingsId, clockMode)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }
}

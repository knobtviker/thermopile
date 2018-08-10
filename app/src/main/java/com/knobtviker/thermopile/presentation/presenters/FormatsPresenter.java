package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.FormatsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class FormatsPresenter extends AbstractPresenter implements FormatsContract.Presenter {

    private final FormatsContract.View view;

    private final SettingsRepository settingsRepository;

    public FormatsPresenter(@NonNull final FormatsContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void saveFormatDate(long settingsId, @NonNull String item) {
        compositeDisposable.add(
            settingsRepository
                .saveFormatDate(settingsId, item)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveFormatTime(long settingsId, @NonNull String item) {
        compositeDisposable.add(
            settingsRepository
                .saveFormatTime(settingsId, item)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }
}

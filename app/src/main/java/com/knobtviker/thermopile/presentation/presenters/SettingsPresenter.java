package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class SettingsPresenter extends AbstractPresenter implements SettingsContract.Presenter {

    private final SettingsContract.View view;

    private final SettingsRepository settingsRepository;

    public SettingsPresenter(@NonNull final SettingsContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void load() {
        compositeDisposable.add(
            settingsRepository
                .load()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    view::onLoad,
                    this::error
                )
        );
    }
}

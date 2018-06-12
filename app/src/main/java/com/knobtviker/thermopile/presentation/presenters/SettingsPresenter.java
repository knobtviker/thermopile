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
            .repository();
    }

    @Override
    public void load() {
        started();

        compositeDisposable.add(
            settingsRepository
                .load()
                .subscribe(
                    settings -> {
                        view.onLoad(settings.get(0));
                    },
                    this::error,
                    this::completed
                )
        );
    }
}

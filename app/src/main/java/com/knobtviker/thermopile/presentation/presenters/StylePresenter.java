package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class StylePresenter extends AbstractPresenter<StyleContract.View> implements StyleContract.Presenter {

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public StylePresenter(
        @NonNull final StyleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
        ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
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

    @Override
    public void saveTheme(long settingsId, int value) {
        compositeDisposable.add(
            settingsRepository
                .saveTheme(settingsId, value)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveScreensaverTimeout(long settingsId, int value) {
        compositeDisposable.add(
            settingsRepository
                .saveScreensaverTimeout(settingsId, value)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }
}

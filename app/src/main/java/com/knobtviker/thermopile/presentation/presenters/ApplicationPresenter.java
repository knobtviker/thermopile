package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import com.knobtviker.android.things.device.RxScreenManager;
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.di.components.domain.DaggerSchedulerProviderComponent;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter extends AbstractPresenter implements ApplicationContract.Presenter {

    private final ApplicationContract.View view;

    private AtmosphereRepository atmosphereRepository;

    private RxScreenManager rxScreenManager;

    @Nullable
    private Disposable screensaverDisposable;

    private Scheduler scheduler;

    public ApplicationPresenter(@NonNull final ApplicationContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
        scheduler = DaggerSchedulerProviderComponent.create().scheduler().screensaver;

        rxScreenManager = RxScreenManager.create(Display.DEFAULT_DISPLAY);
    }

    @Override
    public void addListeners() {
        //DO NOTHING
    }

    @Override
    public void removeListeners() {
        //DO NOTHING
    }

    @Override
    public void tick() {
        compositeDisposable.add(
            Observable
                .interval(1L, TimeUnit.SECONDS)
                .subscribe(
                    ack -> view.onTick(),
                    this::error
                )
        );
    }

    @Override
    public void createScreensaver() {
//        //TODO: Timer delay for screensaver should be loaded from Settings.
        screensaverDisposable = Completable
            .timer(60, TimeUnit.SECONDS, scheduler)
            .observeOn(scheduler)
            .subscribe(
                view::showScreensaver,
                this::error
            );
    }

    @Override
    public void destroyScreensaver() {
        if (screensaverDisposable != null && !screensaverDisposable.isDisposed()) {
            screensaverDisposable.dispose();
            screensaverDisposable = null;
        }
    }

    @Override
    public void initScreen(int density, int rotation, long timeout) {
        Completable.concatArray(
            rxScreenManager.lockRotation(rotation),
            rxScreenManager.displayDensity(density),
            rxScreenManager.screenOffTimeout(timeout, TimeUnit.MILLISECONDS)
        )
            .subscribe(
                this::completed,
                this::error
            );
    }

    @Override
    public void brightness(int brightness) {
        Completable.concatArray(
            rxScreenManager.brightnessMode(RxScreenManager.MANUAL),
            rxScreenManager.brightness(255)
        )
            .subscribe(
                this::completed,
                this::error
            );
    }

    @Override
    public void saveAtmosphere(@NonNull final Atmosphere atmosphere) {
        atmosphereRepository.saveAtmosphere(atmosphere);
    }
}

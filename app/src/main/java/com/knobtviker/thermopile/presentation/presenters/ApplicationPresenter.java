package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter extends AbstractPresenter<ApplicationContract.View> implements ApplicationContract.Presenter {

    @NonNull
    private final AtmosphereRepository atmosphereRepository;

    @NonNull
    private final SettingsRepository settingsRepository;

    @Nullable
    private Disposable screensaverDisposable;

    @Inject
    public ApplicationPresenter(
        @NonNull final ApplicationContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.atmosphereRepository = atmosphereRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void createScreensaver() {
        screensaverDisposable = settingsRepository
            .load()
            .map(settings -> settings.screensaverDelay)
            .flatMapCompletable(delay -> Completable.timer(delay, TimeUnit.SECONDS, schedulers.screensaver))
            .observeOn(schedulers.screensaver)
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
    public void saveTemperature(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            atmosphereRepository
                .saveTemperature(vendor, name, value)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void savePressure(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.savePressure(vendor, name, value),
                atmosphereRepository.saveAltitude(vendor, name, value)
            )
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveHumidity(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            atmosphereRepository
                .saveHumidity(vendor, name, value)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveAirQuality(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            atmosphereRepository
                .saveAirQuality(vendor, name, value)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveLuminosity(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            atmosphereRepository
                .saveLuminosity(vendor, name, value)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveAcceleration(@NonNull String vendor, @NonNull String name, float[] values) {
        compositeDisposable.add(
            atmosphereRepository
                .saveAcceleration(vendor, name, values)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveAngularVelocity(@NonNull String vendor, @NonNull String name, float[] values) {
        compositeDisposable.add(
            atmosphereRepository
                .saveAngularVelocity(vendor, name, values)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveMagneticField(@NonNull String vendor, @NonNull String name, float[] values) {
        compositeDisposable.add(
            atmosphereRepository
                .saveMagneticField(vendor, name, values)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }
}

package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerAtmosphereRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.schedulers.DaggerSchedulerProviderComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.memory.AtmosphereMemoryDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter extends AbstractPresenter implements ApplicationContract.Presenter {

    private final ApplicationContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    private final SettingsRepository settingsRepository;

    private final Scheduler scheduler;

    @Nullable
    private Disposable screensaverDisposable;

    public ApplicationPresenter(@NonNull final ApplicationContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereRepositoryComponent.builder()
            .memoryDataSource(new AtmosphereMemoryDataSourceModule())
            .localDataSource(new AtmosphereLocalDataSourceModule())
            .build()
            .repository();
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .repository();
        this.scheduler = DaggerSchedulerProviderComponent.create().scheduler().screensaver;
    }

    @Override
    public void createScreensaver() {
        // TODO: Check why this makes OOMs
//        screensaverDisposable = settingsRepository
//            .load()
//            .map(settings -> settings.isEmpty() ? 60 : settings.get(0).screensaverDelay)
//            .flatMapCompletable(delay -> Completable.timer(delay, TimeUnit.SECONDS, scheduler))
        screensaverDisposable = Completable.timer(60, TimeUnit.SECONDS, scheduler)
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
    public void saveTemperature(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveTemperatureInMemory(value),
                atmosphereRepository.saveTemperature(vendor, name, value)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void savePressure(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.savePressureInMemory(value),
                atmosphereRepository.savePressure(vendor, name, value),
                atmosphereRepository.saveAltitudeInMemory(value),
                atmosphereRepository.saveAltitude(vendor, name, value)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void saveHumidity(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveHumidityInMemory(value),
                atmosphereRepository.saveHumidity(vendor, name, value)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void saveAirQuality(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveAirQualityInMemory(value),
                atmosphereRepository.saveAirQuality(vendor, name, value)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void saveLuminosity(@NonNull String vendor, @NonNull String name, float value) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveLuminosityInMemory(value),
                atmosphereRepository.saveLuminosity(vendor, name, value)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void saveAcceleration(@NonNull String vendor, @NonNull String name, float[] values) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveAccelerationInMemory(values),
                atmosphereRepository.saveAcceleration(vendor, name, values)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void saveAngularVelocity(@NonNull String vendor, @NonNull String name, float[] values) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveAngularVelocityInMemory(values),
                atmosphereRepository.saveAngularVelocity(vendor, name, values)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void saveMagneticField(@NonNull String vendor, @NonNull String name, float[] values) {
        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository.saveMagneticFieldInMemory(values),
                atmosphereRepository.saveMagneticField(vendor, name, values)
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void settings() {
        started();

        compositeDisposable.add(
            settingsRepository
                .observe()
                .subscribe(
                    settings -> {
                        view.onSettings(settings.get(0));
                    },
                    this::error,
                    this::completed
                )
        );
    }
}

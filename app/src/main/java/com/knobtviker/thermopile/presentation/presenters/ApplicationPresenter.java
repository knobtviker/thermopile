package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import org.joda.time.DateTimeUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter extends AbstractPresenter implements ApplicationContract.Presenter {

    private final ApplicationContract.View view;

    private AtmosphereRepository atmosphereRepository;

    @Nullable
    private Disposable screensaverDisposable;

    public ApplicationPresenter(@NonNull final ApplicationContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        atmosphereRepository = AtmosphereRepository.getInstance();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        AtmosphereRepository.destroyInstance();
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
    public void createScreensaver() {
//        //TODO: Timer delay for screensaver should be loaded from Settings.
        screensaverDisposable = Completable
            .timer(60, TimeUnit.SECONDS, SchedulerProvider.getInstance().screensaver())
            .observeOn(SchedulerProvider.getInstance().screensaver())
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
    public void saveData(float temperature, float humidity, float pressure, final int temperatureAccuracy, final int humidityAccuracy, final int pressureAccuracy) {
        final Atmosphere atmosphere = new Atmosphere();
        atmosphere.timestamp(DateTimeUtils.currentTimeMillis());
        atmosphere.temperature(temperature);
        atmosphere.humidity(humidity);
        atmosphere.pressure(pressure);
        atmosphere.temperatureAccuracy(temperatureAccuracy);
        atmosphere.humidityAccuracy(humidityAccuracy);
        atmosphere.pressureAccuracy(pressureAccuracy);

        atmosphereRepository.save(atmosphere);
    }
}

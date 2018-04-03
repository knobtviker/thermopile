package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.android.things.contrib.community.support.rxscreenmanager.RxScreenManager;
import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerPeripheralsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.di.components.domain.DaggerSchedulerProviderComponent;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter extends AbstractPresenter implements ApplicationContract.Presenter {

    private final ApplicationContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    private final SettingsRepository settingsRepository;

    private final PeripheralsRepository peripheralsRepository;

    private final RxScreenManager rxScreenManager;

    private final Scheduler scheduler;

    private RealmResults<Settings> resultsSettings;

    @Nullable
    private Disposable screensaverDisposable;

    public ApplicationPresenter(@NonNull final ApplicationContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
        this.scheduler = DaggerSchedulerProviderComponent.create().scheduler().screensaver;
        this.rxScreenManager = RxScreenManager.create(Display.DEFAULT_DISPLAY);
    }

    @Override
    public void addListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.addChangeListener(settings -> {
                if (!settings.isEmpty()) {
                    final Settings result = settings.first();
                    if (result != null) {
                        view.onSettings(result);
                    }
                }
            });
        }
    }

    @Override
    public void removeListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
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
    public void observeMinuteTick(@NonNull Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);

        compositeDisposable.add(
            Observable.defer(() ->
                Observable.create(emitter -> {
                    final BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            emitter.onNext(true);
                        }
                    };

                    context.registerReceiver(receiver, filter);

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            context.unregisterReceiver(receiver);

                            dispose();
                        }
                    });
                })
            )
                .subscribe(
                    item -> view.onMinuteTick(),
                    this::error,
                    this::completed
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
    public void initScreen(int density, int rotation) {
        Completable.concatArray(
            rxScreenManager.lockRotation(rotation),
            rxScreenManager.displayDensity(density)
        )
            .subscribe(
                this::completed,
                this::error
            );
    }

    @Override
    public void brightness(int brightness) {
        rxScreenManager
            .brightness(brightness)
            .subscribe(
                this::completed,
                this::error
            );
    }

    @Override
    public void settings(@NonNull final Realm realm) {
        resultsSettings = settingsRepository.load(realm);

        if (!resultsSettings.isEmpty()) {
            final Settings result = resultsSettings.first();
            if (result != null) {
                view.onSettings(result);
            }
        }
    }

    @Override
    public ImmutableList<I2CDevice> i2cDevices() {
        return ImmutableList.copyOf(peripheralsRepository.probe());
    }

    @Override
    public RealmResults<PeripheralDevice> defaultSensors(@NonNull final Realm realm) {
        return peripheralsRepository.load(realm);
    }

    @Override
    public void saveDefaultSensors(@NonNull List<Integer> foundSensors) {
        peripheralsRepository.save(foundSensors);
    }

    @Override
    public void saveTemperature(@NonNull List<Temperature> items) {
        atmosphereRepository.saveTemperature(items);
    }

    @Override
    public void savePressure(@NonNull List<Pressure> items) {
        atmosphereRepository.savePressure(items);
    }

    @Override
    public void saveHumidity(@NonNull List<Humidity> items) {
        atmosphereRepository.saveHumidity(items);
    }

    @Override
    public void saveAltitude(@NonNull List<Altitude> items) {
        atmosphereRepository.saveAltitude(items);
    }

    @Override
    public void saveAirQuality(@NonNull List<AirQuality> items) {
        atmosphereRepository.saveAirQuality(items);
    }

    @Override
    public void saveAccelerations(@NonNull List<Acceleration> items) {
        atmosphereRepository.saveAccelerations(items);
    }

    @Override
    public void saveAngularVelocities(@NonNull List<AngularVelocity> items) {
        atmosphereRepository.saveAngularVelocities(items);
    }

    @Override
    public void saveMagneticFields(@NonNull List<MagneticField> items) {
        atmosphereRepository.saveMagneticFields(items);
    }
}

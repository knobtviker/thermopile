package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerPeripheralsDataComponent;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;
import com.knobtviker.thermopile.presentation.contracts.SensorsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.reactivex.Observable;


/**
 * Created by bojan on 15/07/2017.
 */

public class SensorsPresenter extends AbstractPresenter implements SensorsContract.Presenter {

    private final SensorsContract.View view;

    private final PeripheralsRepository peripheralsRepository;

    public SensorsPresenter(@NonNull final SensorsContract.View view) {
        super(view);

        this.view = view;
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
    }

    @Override
    public void sensors() {
        started();

        compositeDisposable.add(
            peripheralsRepository
                .load()
                .subscribe(
                    view::onSensors,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void sensorEnabled(final long id, final int type, boolean isEnabled) {
        started();

        compositeDisposable.add(
            peripheralsRepository
                .loadById(id)
                .flatMap(peripheralDevice -> {
                    if (peripheralDevice == null) {
                        return Observable.just(0L);
                    } else {
                        return peripheralsRepository.saveEnabled(peripheralDevice, type, isEnabled);
                    }
                })
                .subscribe(
                    resultId -> {
                    },
                    this::error,
                    this::completed
                )
        );
    }
}

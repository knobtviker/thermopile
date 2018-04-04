package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.di.components.data.DaggerPeripheralsDataComponent;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;
import com.knobtviker.thermopile.presentation.contracts.SensorsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class SensorsPresenter extends AbstractPresenter implements SensorsContract.Presenter {

    private final SensorsContract.View view;

    private final PeripheralsRepository peripheralsRepository;

    private RealmResults<PeripheralDevice> resultsPeripherals;

    public SensorsPresenter(@NonNull final SensorsContract.View view) {
        super(view);

        this.view = view;
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {
        if (resultsPeripherals != null && resultsPeripherals.isValid()) {
            resultsPeripherals.addChangeListener(view::onSensors);
        }
    }

    @Override
    public void removeListeners() {
        if (resultsPeripherals != null && resultsPeripherals.isValid()) {
            resultsPeripherals.removeAllChangeListeners();
        }
    }

    @Override
    public void sensors(@NonNull Realm realm) {
        resultsPeripherals = peripheralsRepository.load(realm);

        if (resultsPeripherals != null && !resultsPeripherals.isEmpty()) {
            view.onSensors(resultsPeripherals);
        }
    }

    @Override
    public void sensorEnabled(@NonNull Realm realm, @NonNull String primaryKey, final int type, boolean isEnabled) {
        final PeripheralDevice peripheralDevice = peripheralsRepository.loadById(realm, primaryKey);
        if (peripheralDevice != null) {
            peripheralsRepository.saveEnabled(peripheralDevice, type, isEnabled);
        }
    }
}

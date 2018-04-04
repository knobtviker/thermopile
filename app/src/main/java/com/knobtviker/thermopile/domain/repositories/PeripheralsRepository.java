package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.sources.local.PeripheralLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.PeripheralRawDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class PeripheralsRepository extends AbstractRepository {

    @Inject
    PeripheralLocalDataSource peripheralLocalDataSource;

    @Inject
    PeripheralRawDataSource peripheralRawDataSource;

    @Inject
    PeripheralsRepository() {
    }

    public List<I2CDevice> probe() {
        return peripheralRawDataSource.load();
    }

    public RealmResults<PeripheralDevice> load(@NonNull final Realm realm) {
        return peripheralLocalDataSource.load(realm);
    }

    @Nullable
    public PeripheralDevice loadById(@NonNull final Realm realm, @NonNull final String id) {
        return peripheralLocalDataSource.loadById(realm, id);
    }

    public void save(@NonNull final List<PeripheralDevice> foundSensors) {
        peripheralLocalDataSource.save(foundSensors);
    }

    public void saveConnected(@NonNull final List<PeripheralDevice> items, final boolean isConnected) {
        peripheralLocalDataSource.saveConnected(items, isConnected);
    }

    public void saveEnabled(@NonNull final PeripheralDevice item, final int type, final boolean isEnabled) {
        peripheralLocalDataSource.saveEnabled(item, type, isEnabled);
    }
}

package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

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

    public RealmResults<PeripheralDevice> load(@NonNull final Realm realm) {
        return peripheralLocalDataSource.load(realm);
    }

    public List<I2CDevice> probe() {
        return peripheralRawDataSource.load();
    }

    public void save(@NonNull final List<Integer> foundSensors) {
        peripheralLocalDataSource.save(foundSensors);
    }
}

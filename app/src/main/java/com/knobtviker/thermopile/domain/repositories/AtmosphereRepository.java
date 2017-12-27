package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.PressureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.TemperatureLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends AbstractRepository {

    @Inject
    TemperatureLocalDataSource temperatureLocalDataSource;

    @Inject
    HumidityLocalDataSource humidityLocalDataSource;

    @Inject
    PressureLocalDataSource pressureLocalDataSource;

    @Inject
    AtmosphereRepository() {
    }

    public void saveTemperatures(@NonNull final List<Temperature> items) {
        temperatureLocalDataSource.save(items);
    }

    public RealmResults<Temperature> latestTemperature(@NonNull final Realm realm) {
        return temperatureLocalDataSource.latest(realm);
    }

    public void saveHumidities(@NonNull final List<Humidity> items) {
        humidityLocalDataSource.save(items);
    }

    public RealmResults<Humidity> latestHumidity(@NonNull final Realm realm) {
        return humidityLocalDataSource.latest(realm);
    }

    public void savePressures(@NonNull final List<Pressure> items) {
        pressureLocalDataSource.save(items);
    }

    public RealmResults<Pressure> latestPressure(@NonNull final Realm realm) {
        return pressureLocalDataSource.latest(realm);
    }
}

package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.PressureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.TemperatureLocalDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends AbstractRepository {

    @Inject
    AtmosphereLocalDataSource atmosphereLocalDataSource;

    @Inject
    TemperatureLocalDataSource temperatureLocalDataSource;

    @Inject
    HumidityLocalDataSource humidityLocalDataSource;

    @Inject
    PressureLocalDataSource pressureLocalDataSource;

    @Inject
    AtmosphereRepository() {
    }

    public RealmResults<Temperature> latestTemperature(@NonNull final Realm realm) {
        return temperatureLocalDataSource.latest(realm);
    }

    public RealmResults<Humidity> latestHumidity(@NonNull final Realm realm) {
        return humidityLocalDataSource.latest(realm);
    }

    public RealmResults<Pressure> latestPressure(@NonNull final Realm realm) {
        return pressureLocalDataSource.latest(realm);
    }

    public void saveAtmosphere(@NonNull final Atmosphere item) {
        atmosphereLocalDataSource.save(item);
    }
}

package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.AirQualityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AltitudeLocalDataSource;
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
    AltitudeLocalDataSource altitudeLocalDataSource;

    @Inject
    AirQualityLocalDataSource airQualityLocalDataSource;

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

    public RealmResults<Altitude> latestAltitude(@NonNull final Realm realm) {
        return altitudeLocalDataSource.latest(realm);
    }

    public RealmResults<AirQuality> latestAirQuality(@NonNull final Realm realm) {
        return airQualityLocalDataSource.latest(realm);
    }

    public void saveTemperature(@NonNull final List<Temperature> items) {
        temperatureLocalDataSource.save(items);
    }

    public void savePressure(@NonNull final List<Pressure> items) {
        pressureLocalDataSource.save(items);
    }

    public void saveHumidity(@NonNull final List<Humidity> items) {
        humidityLocalDataSource.save(items);
    }

    public void saveAltitude(@NonNull final List<Altitude> items) {
        altitudeLocalDataSource.save(items);
    }

    public void saveAirQuality(@NonNull final List<AirQuality> items) {
        airQualityLocalDataSource.save(items);
    }
}

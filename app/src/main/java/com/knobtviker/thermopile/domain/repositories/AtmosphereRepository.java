package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Luminosity;
import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.AccelerationLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AirQualityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AltitudeLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AngularVelocityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.LuminosityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.MagneticFieldLocalDataSource;
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
    LuminosityLocalDataSource luminosityLocalDataSource;

    @Inject
    AccelerationLocalDataSource accelerationLocalDataSource;

    @Inject
    AngularVelocityLocalDataSource angularVelocityLocalDataSource;

    @Inject
    MagneticFieldLocalDataSource magneticFieldLocalDataSource;

    @Inject
    AtmosphereRepository() {
    }

    public RealmResults<Temperature> loadTemperature(@NonNull final Realm realm) {
        return temperatureLocalDataSource.load(realm);
    }

    public RealmResults<Humidity> loadHumidity(@NonNull final Realm realm) {
        return humidityLocalDataSource.load(realm);
    }

    public RealmResults<Pressure> loadPressure(@NonNull final Realm realm) {
        return pressureLocalDataSource.load(realm);
    }

    public RealmResults<Altitude> loadAltitude(@NonNull final Realm realm) {
        return altitudeLocalDataSource.load(realm);
    }

    public RealmResults<AirQuality> loadAirQuality(@NonNull final Realm realm) {
        return airQualityLocalDataSource.load(realm);
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

    public void saveLuminosity(@NonNull final List<Luminosity> items) {
        luminosityLocalDataSource.save(items);
    }

    public void saveAccelerations(@NonNull final List<Acceleration> items) {
        accelerationLocalDataSource.save(items);
    }

    public void saveAngularVelocities(@NonNull final List<AngularVelocity> items) {
        angularVelocityLocalDataSource.save(items);
    }

    public void saveMagneticFields(@NonNull final List<MagneticField> items) {
        magneticFieldLocalDataSource.save(items);
    }
}

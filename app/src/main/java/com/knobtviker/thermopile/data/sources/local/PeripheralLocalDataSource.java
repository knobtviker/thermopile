package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class PeripheralLocalDataSource extends AbstractLocalDataSource<PeripheralDevice> {

    @Inject
    public PeripheralLocalDataSource() {
        super(PeripheralDevice.class);
    }


    @Override
    public Observable<List<PeripheralDevice>> observe() {
        return super.observe(
            box.query()
                .order(PeripheralDevice_.id)
                .build()
        );
    }

    @Override
    public Observable<List<PeripheralDevice>> query() {
        return super.query(
            box.query()
                .order(PeripheralDevice_.id)
                .build()
        );
    }

    @Override
    public Observable<PeripheralDevice> queryById(long id) {
        return super.queryById(
            box.query()
                .equal(PeripheralDevice_.id, id)
                .build()
        );
    }

    public Completable saveConnected(@NonNull final List<PeripheralDevice> items, final boolean isConnected) {
        items.forEach(peripheralDevice -> peripheralDevice.connected = isConnected);
        return super.save(items);
    }

    public Observable<Long> saveEnabled(@NonNull final PeripheralDevice item, final int type, final boolean isEnabled) {
            switch (type) {
                case Constants.TYPE_TEMPERATURE:
                    item.enabledTemperature = isEnabled;
                    break;
                case Constants.TYPE_PRESSURE:
                    item.enabledPressure = isEnabled;
                    break;
                case Constants.TYPE_HUMIDITY:
                    item.enabledHumidity = isEnabled;
                    break;
                case Constants.TYPE_AIR_QUALITY:
                    item.enabledAirQuality = isEnabled;
                    break;
                case Constants.TYPE_LUMINOSITY:
                    item.enabledLuminosity = isEnabled;
                    break;
                case Constants.TYPE_ACCELERATION:
                    item.enabledAcceleration = isEnabled;
                    break;
                case Constants.TYPE_ANGULAR_VELOCITY:
                    item.enabledAngularVelocity = isEnabled;
                    break;
                case Constants.TYPE_MAGNETIC_FIELD:
                    item.enabledMagneticField = isEnabled;
                    break;
            }

            return super.save(item);
    }
}

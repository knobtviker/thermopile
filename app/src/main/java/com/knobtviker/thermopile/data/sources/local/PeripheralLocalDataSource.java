package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.data.sources.local.implemenatation.DeviceLocalDataSource;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;

/**
 * Created by bojan on 26/06/2017.
 */

public class PeripheralLocalDataSource extends DeviceLocalDataSource<PeripheralDevice> {

    @Inject
    public PeripheralLocalDataSource() {
        super(PeripheralDevice.class);
    }

    @Override
    public PeripheralDevice loadById(@NonNull Realm realm, String id) {
        return super.loadById(realm, id);
    }

    @Override
    public void save(@NonNull final List<PeripheralDevice> items) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(items));
        realm.close();
    }

    @Override
    public void save(@NonNull final PeripheralDevice item) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(item));
        realm.close();
    }

    public void saveConnected(@NonNull final List<PeripheralDevice> items, final boolean isConnected) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            items.forEach(peripheralDevice -> peripheralDevice.connected(isConnected));

            realm1.insertOrUpdate(items);
        });
        realm.close();
    }

    public void saveEnabled(@NonNull final PeripheralDevice item, final int type, final boolean isEnabled) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            switch (type) {
                case Constants.TYPE_TEMPERATURE:
                    item.enabledTemperature(isEnabled);
                    break;
                case Constants.TYPE_PRESSURE:
                    item.enabledPressure(isEnabled);
                    break;
                case Constants.TYPE_HUMIDITY:
                    item.enabledHumidity(isEnabled);
                    break;
                case Constants.TYPE_AIR_QUALITY:
                    item.enabledAirQuality(isEnabled);
                    break;
                case Constants.TYPE_LUMINOSITY:
                    item.enabledLuminosity(isEnabled);
                    break;
                case Constants.TYPE_ACCELERATION:
                    item.enabledAcceleration(isEnabled);
                    break;
                case Constants.TYPE_ANGULAR_VELOCITY:
                    item.enabledAngularVelocity(isEnabled);
                    break;
                case Constants.TYPE_MAGNETIC_FIELD:
                    item.enabledMagneticField(isEnabled);
                    break;
            }

            realm1.insertOrUpdate(item);
        });
        realm.close();
    }
}

package com.knobtviker.thermopile.data.sources.local;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.data.sources.local.implemenatation.DeviceLocalDataSource;

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

    public void saveConnectedAndEnabled(@NonNull final List<PeripheralDevice> items, final boolean isConnected, final boolean isEnabled) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            items.forEach(peripheralDevice -> {
                peripheralDevice.connected(isConnected);
                peripheralDevice.enabled(isEnabled);
            });

            realm1.insertOrUpdate(items);
        });
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

    public void saveEnabled(@NonNull final List<PeripheralDevice> items, final boolean isEnabled) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            items.forEach(peripheralDevice -> peripheralDevice.enabled(isEnabled));

            realm1.insertOrUpdate(items);
        });
        realm.close();
    }

    public void saveConnectedAndEnabled(@NonNull final PeripheralDevice item, final boolean isConnected, final boolean isEnabled) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            item.connected(isConnected);
            item.enabled(isEnabled);

            realm1.insertOrUpdate(item);
        });
        realm.close();
    }

    public void saveConnected(@NonNull final PeripheralDevice item, final boolean isConnected) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            item.connected(isConnected);

            realm1.insertOrUpdate(item);
        });
        realm.close();
    }

    public void saveEnabled(@NonNull final PeripheralDevice item, final boolean isEnabled) {
        final Realm realm = Database.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            item.enabled(isEnabled);

            realm1.insertOrUpdate(item);
        });
        realm.close();
    }
}

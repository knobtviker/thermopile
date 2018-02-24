package com.knobtviker.thermopile.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.AngularVelocity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 18/07/2017.
 */

public interface AngularVelocityDataSource {

    interface Local {

        RealmResults<AngularVelocity> load(@NonNull final Realm realm);

        void save(@NonNull final List<AngularVelocity> items);
    }
}

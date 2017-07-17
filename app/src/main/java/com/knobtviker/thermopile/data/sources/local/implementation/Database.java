package agency.sevenofnine.crosigcorporate.data.sources.local.implementation;


import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.Models;

import java.util.Optional;

import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.meta.EntityModel;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

/**
 * Created by Bojan Komljenovic on 28.9.2016. at 16:59.
 */

public class Database {
    private static final int VERSION = BuildConfig.DATABASE_VERSION;

    private static Optional<Database> INSTANCE = Optional.empty();

    private final ReactiveEntityStore<Persistable> dataStore;

    public static Database getInstance(@NonNull final Context context) {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new Database(context));
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private Database(@NonNull final Context context) {
        final EntityModel models = Models.DEFAULT;
        final DatabaseSource source = new DatabaseSource(context, models, VERSION);
        source.setTableCreationMode(BuildConfig.DEBUG ? TableCreationMode.DROP_CREATE : TableCreationMode.CREATE_NOT_EXISTS);

        final Configuration configuration = new ConfigurationBuilder(source, models)
            .setEntityCache(null)
            .setStatementCacheSize(0)
            .build();
        dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
    }

    public ReactiveEntityStore<Persistable> database() {
        return dataStore;
    }
}

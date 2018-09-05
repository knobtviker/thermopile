package com.knobtviker.thermopile.di.modules.presentation;

import android.content.Context;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.sources.local.shared.Database;
import com.knobtviker.thermopile.di.qualifiers.presentation.messengers.ForegroundMessenger;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;
import timber.log.Timber;

@Module
public class ApplicationModule {

    private final ThermopileApplication application;

    public ApplicationModule(@NonNull final ThermopileApplication application) {
        this.application = application;

        if (!TextUtils.isEmpty(BuildConfig.KEY_FABRIC)) {
            Fabric.with(application, new Crashlytics());
        }

        Timber.plant(new Timber.DebugTree());

        AndroidThreeTen.init(application);
    }

    @Provides
    @Singleton
    public ThermopileApplication provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    @ForegroundMessenger
    Messenger provideForegroundMessenger(@NonNull final Context context) {
        return new Messenger(new ThermopileApplication.IncomingHandler(context));
    }

    //    @Provides
    //    @DriversMessenger
    //    Messenger provideDriversMessenger() {
    //
    //    }
    //
    //    @Provides
    //    @FramMessenger
    //    Messenger provideFramMessenger() {
    //
    //    }

    @Provides
    @Singleton
    BoxStore provideDatabase(@NonNull final Context context) {
//        return MyObjectBox.builder()
//            .androidContext(context)
//            .name(BuildConfig.DATABASE_NAME)
//            .build();
        return Database.init(context);
    }

    @Provides
    ApplicationContract.Presenter providePresenter(
        @NonNull final ApplicationContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new ApplicationPresenter(view, atmosphereRepository, settingsRepository, schedulers);
    }

    @Provides
    ApplicationContract.View provideView() {
        return application;
    }
}

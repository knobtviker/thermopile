package com.knobtviker.thermopile.presentation;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.Router;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApp extends Application implements ApplicationContract.View {
    private static final String TAG = ThermopileApp.class.getSimpleName();

    @NonNull
    private ApplicationContract.Presenter presenter;

    //TODO: This needs to be moved to presenter
    @Nullable
    private Disposable screensaverDisposable;

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();
        initStetho();
        initPresenter();
        initCalligraphy();
        initJodaTime();

        createScreensaver();
    }

    public void createScreensaver() {
        //TODO: Timer delay for screensaver should be loaded from Settings.
        screensaverDisposable = Completable.timer(300, TimeUnit.SECONDS, SchedulerProvider.getInstance().screensaver())
            .observeOn(SchedulerProvider.getInstance().screensaver())
            .subscribe(this::showScreensaver);
    }

    public void destroyScreensaver() {
        if (screensaverDisposable != null && !screensaverDisposable.isDisposed()) {
            screensaverDisposable.dispose();
            screensaverDisposable = null;
        }
    }

    private void initRealm() {
        Realm.init(this);
        //TODO: Enable encryption
        final RealmConfiguration config = new RealmConfiguration.Builder()
            .name(BuildConfig.DATABASE_NAME)
            .schemaVersion(BuildConfig.DATABASE_VERSION)
            .deleteRealmIfMigrationNeeded()
            .build();

        Realm.setDefaultConfiguration(config);
    }

    private void initStetho() {
//        Stetho.initialize(
//            Stetho.newInitializerBuilder(this)
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//                .build());
    }

    private void initPresenter() {
        presenter = new ApplicationPresenter(this);
        presenter.subscribe();
        presenter.collectData();
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/WorkSans-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build());
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void showScreensaver() {
        Router.showScreensaver(this);
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO: Propagate to each fragment loading method
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onLuminosityData(float luminosity) {
        //TODO: Normalize luminosity in lux from 0 to 40 000 to 0.0 to 1.0
//        Log.i(TAG, "LUMINOSITY --- "+luminosity/40000.0f);
    }
}

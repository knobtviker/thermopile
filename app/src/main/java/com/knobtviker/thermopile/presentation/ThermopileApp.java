package com.knobtviker.thermopile.presentation;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.stetho.Stetho;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;
import com.knobtviker.thermopile.presentation.activities.MainActivity;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.fragments.ScreensaverFragment;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.Router;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by bojan on 15/07/2017.
 */

public class ThermopileApp extends Application implements ApplicationContract.View, Application.ActivityLifecycleCallbacks {
    private static final String TAG = ThermopileApp.class.getSimpleName();

    @NonNull
    private ApplicationContract.Presenter presenter;

    @Nullable
    private Disposable screensaverDisposable;

    @Nullable
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        initPresenter();
        initCalligraphy();
        initJodaTime();
        initStetho();

        createScreensaver();
    }

    public void createScreensaver() {
        screensaverDisposable = Completable.timer(60, TimeUnit.SECONDS, SchedulerProvider.getInstance().screensaver())
            .observeOn(SchedulerProvider.getInstance().ui())
            .subscribe(this::showScreensaver);
    }

    public void destroyScreensaver() {
        if (screensaverDisposable != null) {
            if (!screensaverDisposable.isDisposed()) {
                screensaverDisposable.dispose();
                screensaverDisposable = null;
            }
        }
    }

    private void initPresenter() {
        presenter = new ApplicationPresenter(this, this);
        presenter.subscribe();
        presenter.startClock();
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

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
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
        //TODO: Propagate to each fragment showError method
    }

    @Override
    public void onClockTick() {
        final DateTime dateTime = new DateTime(DateTimeZone.forID("Europe/Zagreb"));
        if (currentActivity != null) {
            if (currentActivity.getClass() == MainActivity.class) {
                final MainFragment fragment = ((MainFragment) ((MainActivity) currentActivity).findFragment(MainFragment.TAG));
                fragment.setDateTime(dateTime);
                fragment.maybeApplyThresholds(dateTime);
                fragment.moveHourLine(dateTime);
            } else if (currentActivity.getClass() == ScreenSaverActivity.class) {
                final ScreensaverFragment fragment = ((ScreensaverFragment) ((ScreenSaverActivity) currentActivity).findFragment(ScreensaverFragment.TAG));
                fragment.setDateTime(dateTime);
            }

            presenter.atmosphereData();
//            presenter.luminosityData(); //TODO: wait for first I2C hardware to close and than daisy chain this one.
        }
    }

    @Override
    public void onAtmosphereData(@NonNull Atmosphere data) {
        if (currentActivity != null) {
            if (currentActivity.getClass() == MainActivity.class) {
                final MainFragment fragment = ((MainFragment) ((MainActivity) currentActivity).findFragment(MainFragment.TAG));
                fragment.populateData(data);
            } else if (currentActivity.getClass() == ScreenSaverActivity.class) {
                final ScreensaverFragment fragment = ((ScreensaverFragment) ((ScreenSaverActivity) currentActivity).findFragment(ScreensaverFragment.TAG));
                fragment.populateData(data);
            }
        }
    }

    @Override
    public void onLuminosityData(float luminosity) {
        //TODO: Normalize luminosity in lux from 0 to 40 000 to 0.0 to 1.0
//        Log.i(TAG, "LUMINOSITY --- "+luminosity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //DO NOTHING
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //DO NOTHING
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        //DO NOTHING
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //DO NOTHING
    }
}

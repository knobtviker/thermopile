package com.knobtviker.thermopile.presentation;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.domain.schedulers.SchedulerProvider;
import com.knobtviker.thermopile.presentation.utils.Router;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by bojan on 15/07/2017.
 */

public class ThermopileApp extends Application {

    private Disposable screensaver;

    @Override
    public void onCreate() {
        super.onCreate();

        initCalligraphy();
        initJodaTime();
        initStetho();

        createScreensaver();
    }

    public void createScreensaver() {
        screensaver = Completable.timer(60, TimeUnit.SECONDS, SchedulerProvider.getInstance().screensaver())
            .observeOn(SchedulerProvider.getInstance().ui())
            .subscribe(this::showScreensaver);
    }

    public void destroyScreensaver() {
        if (screensaver != null) {
            if (!screensaver.isDisposed()) {
                screensaver.dispose();
                screensaver = null;
            }
        }
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
}

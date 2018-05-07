package com.knobtviker.thermopile.presentation;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.sources.local.implementation.Database;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.FileLoggingTree;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public abstract class AbstractApplication<P extends BasePresenter> extends Application {

    protected P presenter;

    @NonNull
    protected String packageName;

    @Override
    public void onCreate() {
        super.onCreate();

        plantTree();

        memoryClass();
        memoryInfo();
        packageName();
        initCrashlytics();
        initJodaTime();
        initDatabase();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (!Database.getInstance().isClosed()) {
            Database.getInstance().close();
        }

        Database.getInstance().deleteAllFiles();
    }

    private void plantTree() {
        Timber.plant(new FileLoggingTree(this));
    }

    private void packageName() {
        this.packageName = getPackageName();
    }

    private void memoryClass() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final int memoryClass = activityManager.getMemoryClass();

        Timber.i("Memory class: %d", memoryClass); // 256MB for RPi3
    }

    public void memoryInfo() {
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        Timber.i("Memory info -> Available: %d Total: %d Threshold: %d Is low: %s", memoryInfo.availMem, memoryInfo.totalMem, memoryInfo.threshold, memoryInfo.lowMemory);
    }

    private void initCrashlytics() {
        if (!TextUtils.isEmpty(BuildConfig.KEY_FABRIC)) {
            Fabric.with(this, new Crashlytics());
        }
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void initDatabase() {
        Database.init(this);
    }
}

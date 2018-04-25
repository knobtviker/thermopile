package com.knobtviker.thermopile.presentation;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.bugfender.sdk.Bugfender;
import com.facebook.stetho.Stetho;
import com.instabug.library.Instabug;
import com.instabug.library.InstabugColorTheme;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.FileLoggingTree;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.danlew.android.joda.JodaTimeAndroid;

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
//        initCrashlytics();
        initBugfender();
        initInstabug();
        initJodaTime();
        initDatabase();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Database.getDefaultInstance().deleteAll();
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

//    private void initCrashlytics() {
//        Fabric.with(this, new Crashlytics());
//    }

    private void initBugfender() {
        Bugfender.init(this, BuildConfig.KEY_BUGFENDER, BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableCrashReporting();
    }

    private void initInstabug() {
        new Instabug.Builder(this, BuildConfig.KEY_INSTABUG)
            .setInvocationEvent(InstabugInvocationEvent.NONE)
            .build();

        Instabug.setEmailFieldRequired(false, 0);
        Instabug.setCommentFieldRequired(false);
        Instabug.setAttachmentTypesEnabled(true, true, false, false);
        Instabug.setIntroMessageEnabled(false);
        Instabug.setShouldPlayConversationSounds(false);
        Instabug.setEnableInAppNotificationSound(false);
        Instabug.setEnableSystemNotificationSound(false);
        Instabug.setSuccessDialogEnabled(false);
        Instabug.setTheme(InstabugColorTheme.InstabugColorThemeDark);
        Instabug.setPrimaryColor(getColor(R.color.colorAccent));
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void initDatabase() {
        Database.init(this);

        initStetho();
    }

    private void initStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                    RealmInspectorModulesProvider.builder(this)
                        .withLimit(1000)
                        .withDescendingOrder()
                        .build()
                )
                .build());
    }
}

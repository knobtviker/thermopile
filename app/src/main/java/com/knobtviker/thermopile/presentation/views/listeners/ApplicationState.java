package com.knobtviker.thermopile.presentation.views.listeners;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.activities.MainActivity;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

public class ApplicationState implements Application.ActivityLifecycleCallbacks {

    private static ApplicationState INSTANCE = null;

    private int activityCount = 0;

    @NonNull
    private final Listener stateListener;

    public static void init(@NonNull final Application application) {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationState(application);
        }
    }

    private ApplicationState(@NonNull final Application application) {
        application.registerActivityLifecycleCallbacks(this);

        stateListener = (Listener) application;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //DO NOTHING
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityCount == 0) {
            stateListener.onForeground();
        }
        activityCount++;

//        if (activity instanceof MainActivity) {
//            stateListener.onMainActivityShown((MainActivity) activity);
//        }
//        if (activity instanceof ScreenSaverActivity) {
//            stateListener.onScreensaverActivityShown((ScreenSaverActivity) activity);
//        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof MainActivity) {
            stateListener.onMainActivityShown((MainActivity) activity);
        }
        if (activity instanceof ScreenSaverActivity) {
            stateListener.onScreensaverActivityShown((ScreenSaverActivity) activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof MainActivity) {
            stateListener.onMainActivityHidden();
        }
        if (activity instanceof ScreenSaverActivity) {
            stateListener.onScreensaverActivityHidden();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            stateListener.onBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //DO NOTHING
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //DO NOTHING
    }

    public interface Listener {

        void onForeground();

        void onBackground();

        void onMainActivityShown(@NonNull final MainActivity activity);

        void onScreensaverActivityShown(@NonNull final ScreenSaverActivity activity);

        void onMainActivityHidden();

        void onScreensaverActivityHidden();
    }
}

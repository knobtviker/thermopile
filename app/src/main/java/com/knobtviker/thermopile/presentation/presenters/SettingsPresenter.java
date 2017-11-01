package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class SettingsPresenter implements SettingsContract.Presenter {

    private final SettingsContract.View view;

    private SettingsRepository settingsRepository;
    private CompositeDisposable compositeDisposable;

    private RealmResults<Settings> resultsSettings;
    private RealmChangeListener<RealmResults<Settings>> changeListenerSettings;

    public SettingsPresenter(@NonNull final SettingsContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
        settingsRepository = SettingsRepository.getInstance();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeChangeListener(changeListenerSettings);
            resultsSettings = null;
            changeListenerSettings = null;
        }
        SettingsRepository.destroyInstance();
    }

    @Override
    public void error(@NonNull Throwable throwable) {
        completed();

        view.showError(throwable);
    }

    @Override
    public void started() {
        view.showLoading(true);
    }

    @Override
    public void completed() {
        view.showLoading(false);
    }

    @Override
    public void load() {
        started();

        resultsSettings = settingsRepository.load();

        if (resultsSettings != null && resultsSettings.isValid()) {
            changeListenerSettings = thresholdsRealmResults -> {
                if (!thresholdsRealmResults.isEmpty()) {
                    view.onLoad(thresholdsRealmResults.first());
                }
            };
            resultsSettings.addChangeListener(changeListenerSettings);
        }

        completed();
    }

    @Override
    public void saveTimezone(long settingsId, @NonNull String timezone) {
        settingsRepository.saveTimezone(settingsId, timezone);
    }

    @Override
    public void saveClockMode(long settingsId, int clockMode) {
        settingsRepository.saveClockMode(settingsId, clockMode);
    }
}

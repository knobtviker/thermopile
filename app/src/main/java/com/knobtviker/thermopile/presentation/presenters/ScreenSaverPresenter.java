package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.MainThreadDisposable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class ScreenSaverPresenter extends AbstractPresenter implements ScreenSaverContract.Presenter {

    private final ScreenSaverContract.View view;

    private final SettingsRepository settingsRepository;

    private RealmResults<Settings> resultsSettings;

    public ScreenSaverPresenter(@NonNull final ScreenSaverContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.addChangeListener(settings -> {
                if (!settings.isEmpty()) {
                    final Settings result = settings.first();
                    if (result != null) {
                        view.onSettingsChanged(result);
                    }
                }
            });
        }
    }

    @Override
    public void removeListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
    }

    @Override
    public void observeDataChanged(@NonNull Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(String.format("%s.%s", context.getPackageName(), Constants.ACTION_NEW_DATA));

        compositeDisposable.add(
            Observable.defer(() ->
                Observable.create((ObservableEmitter<Atmosphere> emitter) -> {
                    final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
                    final BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.hasExtra(Constants.KEY_ATMOSPHERE)) {
                                emitter.onNext(intent.getParcelableExtra(Constants.KEY_ATMOSPHERE));
                            } else {
                                emitter.onError(new NoSuchFieldException());
                            }
                        }
                    };

                    localBroadcastManager.registerReceiver(receiver, filter);

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            localBroadcastManager.unregisterReceiver(receiver);

                            dispose();
                        }
                    });
                })
            )
                .subscribe(
                    view::onDataChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeDateChanged(@NonNull Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);

        compositeDisposable.add(
            Observable.defer(() ->
                Observable.create(emitter -> {
                    final BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            emitter.onNext(true);
                        }
                    };

                    context.registerReceiver(receiver, filter);

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            context.unregisterReceiver(receiver);

                            dispose();
                        }
                    });
                })
            )
                .subscribe(
                    item -> view.onDateChanged(),
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void settings(@NonNull final Realm realm) {
        started();

        resultsSettings = settingsRepository.load(realm);

        if (!resultsSettings.isEmpty()) {
            final Settings result = resultsSettings.first();
            if (result != null) {
                view.onSettingsChanged(result);
            }
        }

        completed();
    }
}

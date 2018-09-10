package com.knobtviker.thermopile.presentation.presenters;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Preferences;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ScreensaverTimeout;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class StylePresenter extends AbstractPresenter<StyleContract.View> implements StyleContract.Presenter {

    private long settingsId = -1L;

    private int theme = Default.THEME;

    @ScreensaverTimeout
    private int screenSaverTimeout = ScreensaverTimeout._1MIN;

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final SharedPreferences preferences;

    @Inject
    public StylePresenter(
        @NonNull final StyleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers,
        @NonNull final SharedPreferences preferences
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
        this.preferences = preferences;
    }

    @Override
    public void load(@NonNull List<Integer> screenSaverTimeouts) {
        compositeDisposable.add(
            settingsRepository
                .load()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    item -> this.onLoad(item, screenSaverTimeouts),
                    this::error
                )
        );
    }

    @Override
    public void saveTheme(int value) {
        preferences
            .edit()
            .putInt(Preferences.THEME, value)
            .apply();
        compositeDisposable.add(
            settingsRepository
                .saveTheme(settingsId, value)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveScreensaverTimeout(int value) {
        compositeDisposable.add(
            settingsRepository
                .saveScreensaverTimeout(settingsId, value)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    private void onLoad(@NonNull final Settings settings, @NonNull final List<Integer> screenSaverTimeouts) {
        final int prefTheme = preferences.getInt(Preferences.THEME, Default.THEME);
        settingsId = settings.id;
        screenSaverTimeout = settings.screensaverDelay;

        if (theme != prefTheme) {
            theme = prefTheme;
        }
        view.setTheme(theme);

        for (int i = 0; i < screenSaverTimeouts.size(); i++) {
            if (screenSaverTimeouts.get(i) == screenSaverTimeout) {
                view.setScreenSaverTimeout(i);
                break;
            }
        }
    }
}

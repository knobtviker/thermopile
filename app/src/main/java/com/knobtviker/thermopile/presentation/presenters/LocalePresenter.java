package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by bojan on 15/07/2017.
 */

public class LocalePresenter extends AbstractPresenter<LocaleContract.View> implements LocaleContract.Presenter {

    private long settingsId = -1L;

    @NonNull
    private String timezone = Default.TIMEZONE;

    @ClockMode
    private int clockMode = ClockMode._24H;

    @NonNull
    @FormatDate
    private String formatDate = FormatDate.EEEE_DD_MM_YYYY;

    @NonNull
    @FormatTime
    private String formatTime = FormatTime.HH_MM;

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public LocalePresenter(
        @NonNull final LocaleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void load(
        @NonNull List<String> timezones,
        @NonNull List<String> dateFormats,
        @NonNull List<String> timeFormats
    ) {
        compositeDisposable.add(
            settingsRepository
                .load()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    settings -> this.onLoaded(settings, timezones, dateFormats, timeFormats),
                    this::error
                )
        );
    }

    @Override
    public void saveTimezone(@NonNull String item) {
        compositeDisposable.add(
            settingsRepository
                .saveTimezone(settingsId, timezone)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    () -> timezone = item,
                    this::error
                )
        );
    }

    @Override
    public void saveClockMode(@ClockMode int item) {
        compositeDisposable.add(
            settingsRepository
                .saveClockMode(settingsId, item)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    () -> clockMode = item,
                    this::error
                )
        );
    }

    @Override
    public void saveFormatDate(@NonNull @FormatDate String item) {
        compositeDisposable.add(
            settingsRepository
                .saveFormatDate(settingsId, item)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    () -> formatDate = item,
                    this::error
                )
        );
    }

    @Override
    public void saveFormatTime(@NonNull @FormatTime String item) {
        compositeDisposable.add(
            settingsRepository
                .saveFormatTime(settingsId, item)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    () -> formatTime = item,
                    this::error
                )
        );
    }

    private void onLoaded(
        @NonNull final Settings settings,
        @NonNull final List<String> timezones,
        @NonNull final List<String> dateFormats,
        @NonNull final List<String> timeFormats
    ) {
        this.settingsId = settings.id;
        this.timezone = settings.timezone;
        this.clockMode = settings.formatClock;
        this.formatDate = settings.formatDate;
        this.formatTime = settings.formatTime;

        for (int i = 0; i < timezones.size(); i++) {
            if (timezones.get(i).equalsIgnoreCase(timezone)) {
                view.setTimezone(i);
                break;
            }
        }

        switch (clockMode) {
            case ClockMode._12H:
                view.button12hChecked();
                break;
            case ClockMode._24H:
                view.button24hChecked();
                break;
        }

        for (int i = 0; i < dateFormats.size(); i++) {
            if (dateFormats.get(i).equalsIgnoreCase(formatDate)) {
                view.setDateFormat(i);
                break;
            }
        }

        for (int i = 0; i < timeFormats.size(); i++) {
            if (timeFormats.get(i).equalsIgnoreCase(formatTime)) {
                view.setTimeFormat(i);
                break;
            }
        }
    }
}

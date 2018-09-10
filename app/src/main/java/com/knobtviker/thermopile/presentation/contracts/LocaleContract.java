package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface LocaleContract {

    interface View extends BaseView {

        void setTimezone(final int index);

        void button12hChecked();

        void button24hChecked();

        void setDateFormat(int index);

        void setTimeFormat(int index);
    }

    interface Presenter extends BasePresenter {

        void load(
            @NonNull final List<String> timezones,
            @NonNull final List<String> dateFormats,
            @NonNull final List<String> timeFormats
        );

        void saveTimezone(@NonNull final String item);

        void saveClockMode(@ClockMode final int item);

        void saveFormatDate(@NonNull @FormatDate final String item);

        void saveFormatTime(@NonNull @FormatTime final String item);
    }
}

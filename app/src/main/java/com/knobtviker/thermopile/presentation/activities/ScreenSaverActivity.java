package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.shared.base.BaseActivity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Brightness;

/**
 * Created by bojan on 11/06/2017.
 */

public class ScreenSaverActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);

        brightness(Brightness.MINIMUM);
    }

    @Override
    protected void onDestroy() {
        brightness(Brightness.MAXIMUM);

        super.onDestroy();
    }

    private void brightness(@Brightness final float value) {
        final WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = value;
        getWindow().setAttributes(layoutParams);
    }
}

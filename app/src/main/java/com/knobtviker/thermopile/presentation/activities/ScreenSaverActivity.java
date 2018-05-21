package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;

/**
 * Created by bojan on 11/06/2017.
 */

public class ScreenSaverActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);

        brightnessMin();
    }

    @Override
    protected void onDestroy() {
        brightnessMax();

        super.onDestroy();
    }

//    @Override
//    public void finish() {
//        overridePendingTransition(R.anim.no_anim, R.anim.exit_bottom_to_top);
//        super.finish();
//    }

    private void brightnessMin() {
        brightness(0.1f);
    }

    private void brightnessMax() {
        brightness(1.0f);
    }

    private void brightness(final float value) {
        final WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = value;
        getWindow().setAttributes(layoutParams);
    }
}

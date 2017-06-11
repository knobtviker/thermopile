package com.knobtviker.thermopile.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.knobtviker.thermopile.R;

/**
 * Created by bojan on 11/06/2017.
 */

public class ScreenSaverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);

        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 0.2f;
        getWindow().setAttributes(lp);

        new Handler().postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },
            3000
        );

    }
}

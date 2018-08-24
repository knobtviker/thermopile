package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.shared.base.BaseActivity;

import androidx.navigation.Navigation;

/**
 * Created by bojan on 24/08/2018.
 */

public class WirelessActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wireless);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.navigation_host_screensaver).navigateUp();
    }
}

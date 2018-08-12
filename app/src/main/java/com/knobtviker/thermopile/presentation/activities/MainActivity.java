package com.knobtviker.thermopile.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.shared.base.BaseActivity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.RequestCode;
import com.knobtviker.thermopile.presentation.utils.Router;

import timber.log.Timber;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ThermopileApplication.bluetooth.enabled()) {
            Router.enableDiscoverability(this, RequestCode.BLUETOOTH_DISCOVERABILITY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RequestCode.BLUETOOTH_DISCOVERABILITY && resultCode == RESULT_OK) {
            Timber.i(requestCode + " --- " + resultCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

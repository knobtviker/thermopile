package com.knobtviker.thermopile.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showMainFragment();

        new Handler().postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), ScreenSaverActivity.class));
                }
            },
            3000
        );
    }

    private void showMainFragment() {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance(), MainFragment.TAG)
            .commit();
    }
}

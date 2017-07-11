package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;

import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

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
public class MainActivity extends BaseActivity implements MainCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        showMainFragment();
        showSchedule();
    }

    @Override
    public void back() {
        popBackStack();
    }

    @Override
    public void showSchedule() {
        addFragment(ScheduleFragment.newInstance(), ScheduleFragment.TAG, true);
    }

    @Override
    public void showSettings() {
        addFragment(SettingsFragment.newInstance(), SettingsFragment.TAG, true);
    }

    private void showMainFragment() {
        replaceFragment(MainFragment.newInstance(), MainFragment.TAG, false);
    }
}

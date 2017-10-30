package com.knobtviker.thermopile.presentation.activities.implementation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bojan on 10/06/2017.
 */

public abstract class BaseActivity extends Activity {

    private final FragmentManager fragmentManager;

    public BaseActivity() {
        this.fragmentManager = getFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public Fragment findFragment(@NonNull String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    public void popBackStack() {
        fragmentManager.popBackStack();
    }

    @SuppressWarnings("SameParameterValue")
    public void replaceFragment(@NonNull final Fragment fragment, @NonNull final String tag, final boolean shouldAddToBackstack) {
        final FragmentTransaction transaction = buildTransaction()
            .replace(R.id.fragment_container, fragment, tag);

        if (shouldAddToBackstack) {
            transaction.addToBackStack(tag);
        }

        commitTransaction(transaction);
    }

    public void addFragment(@NonNull final Fragment fragment, @NonNull final String tag, final boolean shouldAddToBackstack) {
        if (!fragment.isAdded()) {
            final FragmentTransaction transaction = buildTransaction()
                .add(R.id.fragment_container, fragment, tag);

            if (shouldAddToBackstack) {
                transaction.addToBackStack(tag);
            }

            commitTransaction(transaction);
        }
    }

    @SuppressLint("CommitTransaction")
    private FragmentTransaction buildTransaction() {
        return fragmentManager.beginTransaction();
    }

    private void commitTransaction(@NonNull final FragmentTransaction transaction) {
        transaction.commit();
    }
}

package com.knobtviker.thermopile.presentation.activities;

import android.os.Bundle;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.activities.implementation.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }
}

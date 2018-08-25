package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 25/08/2018.
 */

public class ConnecDialogViewHolder {

    @BindView(R.id.textview_ssid)
    public TextView textViewSSID;

    @BindView(R.id.edittext_password)
    public TextInputEditText editTextPassword;

    @BindView(R.id.button_connect)
    public Button buttonConnect;

    public ConnecDialogViewHolder(@NonNull final View itemView) {

        ButterKnife.bind(this, itemView);
    }
}

package com.knobtviker.thermopile.presentation.views.viewholders;

import android.view.View;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 13/06/2017.
 */

public class DayViewHolder {

    @BindView(R.id.textview_title)
    public TextView textViewTitle;

    public DayViewHolder(View itemView) {

        ButterKnife.bind(this, itemView);
    }
}

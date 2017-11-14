package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 13/06/2017.
 */

public class ColorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.background_view)
    public View backgroundView;

    @BindView(R.id.imageview_selected)
    public ImageView imageViewSelected;

    public ColorViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}

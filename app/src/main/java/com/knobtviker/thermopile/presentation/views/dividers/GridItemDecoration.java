package com.knobtviker.thermopile.presentation.views.dividers;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;
    private final int spanCount;
    private boolean hasEdgeSpacing;

    public static GridItemDecoration create(final int spacing) {
        return create(spacing, 7, false);
    }

    public static GridItemDecoration create(final int spacing, final int spanCount) {
        return create(spacing, spanCount, false);
    }

    public static GridItemDecoration create(final int spacing, final int spanCount, final boolean hasEdgeSpacing) {
        return new GridItemDecoration(spacing, spanCount, hasEdgeSpacing);
    }

    private GridItemDecoration(final int spacing, final int spanCount, final boolean hasEdgeSpacing) {
        this.spacing = spacing;
        this.spanCount = spanCount;
        this.hasEdgeSpacing = hasEdgeSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int frameWidth = (int) ((parent.getWidth() - (float) spacing * (spanCount - 1)) / spanCount);
        final int padding = parent.getWidth() / spanCount - frameWidth;
        final int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();

        if (itemPosition < spanCount) {
            outRect.top = 0;
        } else {
            outRect.top = spacing;
        }

        if (itemPosition % spanCount == 0) {
            outRect.left = 0;
            outRect.right = padding;
            hasEdgeSpacing = true;
        } else if ((itemPosition + 1) % spanCount == 0) {
            hasEdgeSpacing = false;
            outRect.right = 0;
            outRect.left = padding;
        } else if (hasEdgeSpacing) {
            hasEdgeSpacing = false;
            outRect.left = spacing - padding;
            if ((itemPosition + 2) % spanCount == 0) {
                outRect.right = spacing - padding;
            } else {
                outRect.right = spacing / 2;
            }
        } else if ((itemPosition + 2) % spanCount == 0) {
            outRect.left = spacing / 2;
            outRect.right = spacing - padding;
        } else {
            outRect.left = spacing / 2;
            outRect.right = spacing / 2;
        }

        outRect.bottom = 0;
    }
}

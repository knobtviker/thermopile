package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.util.AttributeSet;
import android.widget.Checkable;

public class MaterialToggleButton extends MaterialButton implements Checkable {

    private boolean isChecked = false;
    private boolean broadcasting = false;

    @Nullable
    private OnCheckedChangeListener onCheckedChangeListener;

    @Nullable
    private OnCheckedChangeListener onCheckedChangeWidgetListener;

    public MaterialToggleButton(Context context) {
        super(context);

        setChecked(isChecked);
    }

    public MaterialToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setChecked(isChecked);
    }

    public MaterialToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setChecked(isChecked);
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            refreshDrawableState();

            // Avoid infinite recursions if setChecked() is called from a listener
            if (broadcasting) {
                return;
            }

            broadcasting = true;

            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, isChecked);
            }

            broadcasting = false;
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    void setOnCheckedChangeWidgetListener(@Nullable OnCheckedChangeListener listener) {
        onCheckedChangeWidgetListener = listener;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(MaterialButton buttonView, boolean isChecked);
    }
}

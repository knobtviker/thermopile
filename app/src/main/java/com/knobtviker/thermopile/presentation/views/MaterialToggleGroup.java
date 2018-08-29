package com.knobtviker.thermopile.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.knobtviker.thermopile.R;

public class MaterialToggleGroup extends LinearLayout {

    private int checkedId = -1;

    private boolean protectFromCheckedChange = false;

    @Nullable
    private MaterialToggleButton.OnCheckedChangeListener childOnCheckedChangeListener;

    @Nullable
    private OnCheckedChangeListener onCheckedChangeListener;

    @NonNull
    private PassThroughHierarchyChangeListener passThroughListener;

    public MaterialToggleGroup(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        init();
    }

    public MaterialToggleGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialToggleGroup, R.attr.materialToggleGroupStyle, 0);
        int value = typedArray.getResourceId(R.styleable.MaterialToggleGroup_checkedButton, View.NO_ID);
        if (value != View.NO_ID) {
            checkedId = value;
        }
        final int index = typedArray.getInt(R.styleable.MaterialToggleGroup_orientation, HORIZONTAL);
        setOrientation(index);
        typedArray.recycle();

        init();
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        passThroughListener.onHierarchyChangeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        if (checkedId != -1) {
            protectFromCheckedChange = true;
            setCheckedStateForView(checkedId, true);
            protectFromCheckedChange = false;
            setCheckedId(checkedId);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof MaterialButton) {
            final MaterialToggleButton button = (MaterialToggleButton) child;
            if (button.isChecked()) {
                protectFromCheckedChange = true;
                if (checkedId != -1) {
                    setCheckedStateForView(checkedId, false);
                }
                protectFromCheckedChange = false;
                setCheckedId(button.getId());
            }
        }

        super.addView(child, index, params);
    }

    public void check(@IdRes int id) {
        // don't even bother
        if (id != -1 && (id == checkedId)) {
            return;
        }

        if (checkedId != -1) {
            setCheckedStateForView(checkedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    private void init() {
        childOnCheckedChangeListener = new CheckedStateTracker();
        passThroughListener = new PassThroughHierarchyChangeListener();

        super.setOnHierarchyChangeListener(passThroughListener);
    }

    private void setCheckedId(@IdRes int id) {
        checkedId = id;

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checkedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean activated) {
        final View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof MaterialButton) {
            ((MaterialButton) checkedView).setActivated(activated);
        }
    }

    @IdRes
    public int getCheckedRadioButtonId() {
        return checkedId;
    }

    public void clearCheck() {
        check(-1);
    }

    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MaterialToggleGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MaterialToggleGroup.LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return MaterialToggleGroup.class.getName();
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }


        public LayoutParams(int w, int h) {
            super(w, h);
        }


        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }


        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }


        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @Override
        protected void setBaseAttributes(TypedArray a,
            int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(MaterialToggleGroup group, @IdRes int checkedId);
    }

    private class CheckedStateTracker implements MaterialToggleButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(MaterialButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (protectFromCheckedChange) {
                return;
            }

            protectFromCheckedChange = true;
            if (checkedId != -1) {
                setCheckedStateForView(checkedId, false);
            }
            protectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id);
        }
    }

    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {

        private ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener;

        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == MaterialToggleGroup.this && child instanceof MaterialToggleButton) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }
                ((MaterialToggleButton) child).setOnCheckedChangeListener(childOnCheckedChangeListener);
            }

            if (onHierarchyChangeListener != null) {
                onHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == MaterialToggleGroup.this && child instanceof MaterialToggleButton) {
                ((MaterialToggleButton) child).setOnCheckedChangeWidgetListener(null);
            }

            if (onHierarchyChangeListener != null) {
                onHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}

package com.knobtviker.thermopile.presentation.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.knobtviker.thermopile.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

import static com.knobtviker.thermopile.presentation.views.DiscreteSeekBar.Builder.dp2px;
import static com.knobtviker.thermopile.presentation.views.DiscreteSeekBar.Builder.sp2px;

public class DiscreteSeekBar extends View {

    static final int NONE = -1;

    @IntDef({NONE, TextPosition.SIDES, TextPosition.BOTTOM_SIDES, TextPosition.BELOW_SECTION_MARK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextPosition {
        int SIDES = 0, BOTTOM_SIDES = 1, BELOW_SECTION_MARK = 2;
    }

    // Values
    @Nullable
    private String unit; // unit
    private float min; // min
    private float max; // max
    private float progress; // real time value
    private boolean isFloatType; // support for float type output

    // Sizes
    private int trackSize; // height of right-track(on the right of thumb)
    private int secondTrackSize; // height of left-track(on the left of thumb)
    private int thumbRadius; // radius of thumb
    private int thumbRadiusOnDragging; // radius of thumb when be dragging
    private int trackColor; // color of right-track
    private int secondTrackColor; // color of left-track
    private int thumbColor; // color of thumb

    // Section settings
    private int sectionCount; // shares of whole progress(max - min)
    private boolean isShowSectionMark; // show demarcation points or not
    private boolean isAutoAdjustSectionMark; // auto scroll to the nearest section_mark or not
    private boolean isShowSectionText; // show section-text or not
    private int sectionTextSize; // text size of section-text
    private int sectionTextColor; // text color of section-text
    @TextPosition
    private int sectionTextPosition = NONE; // text position of section-text relative to track
    private int sectionTextInterval; // the interval of two section-text

    // Thumb settings
    private boolean isShowThumbText; // show real time progress-text under thumb or not
    private int thumbTextSize; // text size of progress-text
    private int thumbTextColor; // text color of progress-text
    private boolean isShowProgressInFloat; // show bubble-progress in float or not
    private boolean isAlwaysShowBubble; // bubble shows all time
    private long alwaysShowBubbleDelay; // the delay duration before bubble shows all the time
    private boolean isHideBubble; // hide bubble
    private int bubbleColor;// color of bubble
    private int bubbleTextSize; // text size of bubble-progress
    private int bubbleTextColor; // text color of bubble-progress

    // Track settings
    private boolean isTouchToSeek; // touch anywhere on track to quickly seek
    private boolean isSeekStepSection; // seek one step by one section, the progress is discrete
    private boolean isSeekBySection; // seek by section, the progress may not be linear
    private long animatioDuration; // duration of animation
    private boolean isRtl; // right to left

    // Calculated settings
    private float delta; // max - min
    private float sectionValue; // (delta / sectionCount)
    private float thumbCenterX; // X coordinate of thumb's center
    private float trackLength; // pixel length of whole track
    private float sectionOffset; // pixel length of one section
    private boolean isThumbOnDragging; // is thumb on dragging or not
    private int textSpace; // space between text and track
    private boolean triggerBubbleShowing;
    private SparseArray<String> sectionTexts = new SparseArray<>();
    private float preThumbCenterX;
    private boolean triggerSeekBySection;
    private float left; // space between left of track and left of the view
    private float right; // space between right of track and left of the view
    private Paint paint;
    private Rect rectText;
    private int[] point = new int[2];
    private boolean isTouchToSeekAnimEnd = true;
    private float previousSectionValue; // previous SectionValue
    private float dx;

    private BubbleView bubbleView;
    private int bubbleRadius;
    private float bubbleCenterRawSolidX;
    private float bubbleCenterRawSolidY;
    private float bubbleCenterRawX;
    private OnProgressChangedListener progressListener; // progress changing listener
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Builder builder;

    public DiscreteSeekBar(Context context) {
        this(context, null);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DiscreteSeekBar, defStyleAttr, 0);
        unit = typedArray.getString(R.styleable.DiscreteSeekBar_discrete_unit);
        min = typedArray.getFloat(R.styleable.DiscreteSeekBar_discrete_min, 0.0f);
        max = typedArray.getFloat(R.styleable.DiscreteSeekBar_discrete_max, 100.0f);
        progress = typedArray.getFloat(R.styleable.DiscreteSeekBar_discrete_progress, min);
        isFloatType = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_is_float_type, false);
        trackSize = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_track_size, dp2px(2));
        secondTrackSize = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_second_track_size, trackSize + dp2px(2));
        thumbRadius = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_thumb_radius, secondTrackSize + dp2px(2));
        thumbRadiusOnDragging = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_thumb_radius_on_dragging, secondTrackSize * 2);
        sectionCount = typedArray.getInteger(R.styleable.DiscreteSeekBar_discrete_section_count, 10);
        trackColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_track_color, ContextCompat.getColor(context, R.color.colorPrimary));
        secondTrackColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_second_track_color, ContextCompat.getColor(context, R.color.colorAccent));
        thumbColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_thumb_color, secondTrackColor);
        isShowSectionText = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_show_section_text, false);
        sectionTextSize = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_section_text_size, sp2px(14));
        sectionTextColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_section_text_color, trackColor);
        isSeekStepSection = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_seek_step_section, false);
        isSeekBySection = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_seek_by_section, false);

        final int pos = typedArray.getInteger(R.styleable.DiscreteSeekBar_discrete_section_text_position, NONE);
        switch (pos) {
            case 0:
                sectionTextPosition = TextPosition.SIDES;
                break;
            case 1:
                sectionTextPosition = TextPosition.BOTTOM_SIDES;
                break;
            case 2:
                sectionTextPosition = TextPosition.BELOW_SECTION_MARK;
                break;
            default:
                sectionTextPosition = NONE;
                break;
        }

        sectionTextInterval = typedArray.getInteger(R.styleable.DiscreteSeekBar_discrete_section_text_interval, 1);
        isShowThumbText = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_show_thumb_text, false);
        thumbTextSize = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_thumb_text_size, sp2px(14));
        thumbTextColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_thumb_text_color, secondTrackColor);
        bubbleColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_bubble_color, secondTrackColor);
        bubbleTextSize = typedArray.getDimensionPixelSize(R.styleable.DiscreteSeekBar_discrete_bubble_text_size, sp2px(14));
        bubbleTextColor = typedArray.getColor(R.styleable.DiscreteSeekBar_discrete_bubble_text_color, Color.WHITE);
        isShowSectionMark = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_show_section_mark, false);
        isAutoAdjustSectionMark = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_auto_adjust_section_mark, false);
        isShowProgressInFloat = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_show_progress_in_float, false);

        int duration = typedArray.getInteger(R.styleable.DiscreteSeekBar_discrete_anim_duration, -1);
        animatioDuration = duration < 0 ? 200 : duration;

        isTouchToSeek = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_touch_to_seek, false);
        isAlwaysShowBubble = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_always_show_bubble, false);
        duration = typedArray.getInteger(R.styleable.DiscreteSeekBar_discrete_always_show_bubble_delay, 0);
        alwaysShowBubbleDelay = duration < 0 ? 0 : duration;
        isHideBubble = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_hide_bubble, false);
        isRtl = typedArray.getBoolean(R.styleable.DiscreteSeekBar_discrete_rtl, false);
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);

        rectText = new Rect();
        textSpace = dp2px(2);

        initBuilderByPriority();

        if (isHideBubble)
            return;

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // init BubbleView
        bubbleView = new BubbleView(context);
        bubbleView.setProgressText(String.format("%s%s", isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)));

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        calculateRadiusOfBubble();
    }

    private void initBuilderByPriority() {
        if (min == max) {
            min = 0.0f;
            max = 100.0f;
        }
        if (min > max) {
            final float tmp = max;
            max = min;
            min = tmp;
        }
        if (progress < min) {
            progress = min;
        }
        if (progress > max) {
            progress = max;
        }
        if (secondTrackSize < trackSize) {
            secondTrackSize = trackSize + dp2px(2);
        }
        if (thumbRadius <= secondTrackSize) {
            thumbRadius = secondTrackSize + dp2px(2);
        }
        if (thumbRadiusOnDragging <= secondTrackSize) {
            thumbRadiusOnDragging = secondTrackSize * 2;
        }
        if (sectionCount <= 0) {
            sectionCount = 10;
        }
        delta = max - min;
        sectionValue = delta / sectionCount;

        if (sectionValue < 1) {
            isFloatType = true;
        }
        if (isFloatType) {
            isShowProgressInFloat = true;
        }
        if (sectionTextPosition != NONE) {
            isShowSectionText = true;
        }
        if (isShowSectionText) {
            if (sectionTextPosition == NONE) {
                sectionTextPosition = TextPosition.SIDES;
            }
            if (sectionTextPosition == TextPosition.BELOW_SECTION_MARK) {
                isShowSectionMark = true;
            }
        }
        if (sectionTextInterval < 1) {
            sectionTextInterval = 1;
        }

        initSectionTexts();

        if (isSeekStepSection) {
            isSeekBySection = false;
            isAutoAdjustSectionMark = false;
        }
        if (isAutoAdjustSectionMark && !isShowSectionMark) {
            isAutoAdjustSectionMark = false;
        }
        if (isSeekBySection) {
            previousSectionValue = min;
            if (progress != min) {
                previousSectionValue = sectionValue;
            }
            isShowSectionMark = true;
            isAutoAdjustSectionMark = true;
        }
        if (isHideBubble) {
            isAlwaysShowBubble = false;
        }
        if (isAlwaysShowBubble) {
            setProgress(progress);
        }

        thumbTextSize = isFloatType || isSeekBySection || (isShowSectionText && sectionTextPosition == TextPosition.BELOW_SECTION_MARK) ? sectionTextSize : thumbTextSize;
    }

    /**
     * Calculate radius of bubble according to min and max
     */
    private void calculateRadiusOfBubble() {
        paint.setTextSize(bubbleTextSize);

        // Calculate the width of the text that needs to be displayed in the bubble at both ends of the slide.
        // Compare the maximum value to the radius of the bubble.
        String text;
        if (isShowProgressInFloat) {
            text = float2String(isRtl ? max : min);
        } else {
            if (isRtl) {
                text = isFloatType ? float2String(max) : String.valueOf((int) max);
            } else {
                text = isFloatType ? float2String(min) : String.valueOf((int) min);
            }
        }
        paint.getTextBounds(text, 0, text.length(), rectText);
        int w1 = (rectText.width() + textSpace * 2) >> 1;

        if (isShowProgressInFloat) {
            text = float2String(isRtl ? min : max);
        } else {
            if (isRtl) {
                text = isFloatType ? float2String(min) : String.valueOf((int) min);
            } else {
                text = isFloatType ? float2String(max) : String.valueOf((int) max);
            }
        }
        paint.getTextBounds(text, 0, text.length(), rectText);
        final int w2 = (rectText.width() + textSpace * 2) >> 1;

        bubbleRadius = dp2px(14); // default 14dp
        final int max = Math.max(bubbleRadius, Math.max(w1, w2));
        bubbleRadius = max + textSpace;
    }

    private void initSectionTexts() {
        final boolean ifBelowSection = sectionTextPosition == TextPosition.BELOW_SECTION_MARK;
        final boolean ifInterval = sectionTextInterval > 1 && sectionCount % 2 == 0;
        float sectionValue;
        for (int i = 0; i <= sectionCount; i++) {
            sectionValue = isRtl ? max - this.sectionValue * i : min + this.sectionValue * i;

            if (ifBelowSection) {
                if (ifInterval) {
                    if (i % sectionTextInterval == 0) {
                        sectionValue = isRtl ? max - this.sectionValue * i : min + this.sectionValue * i;
                    } else {
                        continue;
                    }
                }
            } else {
                if (i != 0 && i != sectionCount) {
                    continue;
                }
            }

            sectionTexts.put(i, isFloatType ? float2String(sectionValue) : (int) sectionValue + "");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = thumbRadiusOnDragging * 2; // The default height is the diameter of the thumb circle when dragging
        if (isShowThumbText) {
            paint.setTextSize(thumbTextSize);
            paint.getTextBounds("j", 0, 1, rectText); // j is the highest of all letters and numbers
            height += rectText.height(); // If real-time progress is displayed, the original text is added to the progress text height and interval
        }
        if (isShowSectionText && sectionTextPosition >= TextPosition.BOTTOM_SIDES) { // If the Section value is displayed under the track, the comparison takes a larger value
            paint.setTextSize(sectionTextSize);
            paint.getTextBounds("j", 0, 1, rectText);
            height = Math.max(height, thumbRadiusOnDragging * 2 + rectText.height());
        }
        height += textSpace * 2;
        setMeasuredDimension(resolveSize(dp2px(180), widthMeasureSpec), height);

        left = getPaddingLeft() + thumbRadiusOnDragging;
        right = getMeasuredWidth() - getPaddingRight() - thumbRadiusOnDragging;

        if (isShowSectionText) {
            paint.setTextSize(sectionTextSize);

            if (sectionTextPosition == TextPosition.SIDES) {
                String text = sectionTexts.get(0);
                paint.getTextBounds(text, 0, text.length(), rectText);
                left += (rectText.width() + textSpace);

                text = sectionTexts.get(sectionCount);
                paint.getTextBounds(text, 0, text.length(), rectText);
                right -= (rectText.width() + textSpace);
            } else if (sectionTextPosition >= TextPosition.BOTTOM_SIDES) {
                String text = sectionTexts.get(0);
                paint.getTextBounds(text, 0, text.length(), rectText);
                float max = Math.max(thumbRadiusOnDragging, rectText.width() / 2f);
                left = getPaddingLeft() + max + textSpace;

                text = sectionTexts.get(sectionCount);
                paint.getTextBounds(text, 0, text.length(), rectText);
                max = Math.max(thumbRadiusOnDragging, rectText.width() / 2f);
                right = getMeasuredWidth() - getPaddingRight() - max - textSpace;
            }
        } else if (isShowThumbText && sectionTextPosition == NONE) {
            paint.setTextSize(thumbTextSize);

            String text = sectionTexts.get(0);
            paint.getTextBounds(text, 0, text.length(), rectText);
            float max = Math.max(thumbRadiusOnDragging, rectText.width() / 2f);
            left = getPaddingLeft() + max + textSpace;

            text = sectionTexts.get(sectionCount);
            paint.getTextBounds(text, 0, text.length(), rectText);
            max = Math.max(thumbRadiusOnDragging, rectText.width() / 2f);
            right = getMeasuredWidth() - getPaddingRight() - max - textSpace;
        }

        trackLength = right - left;
        sectionOffset = trackLength * 1f / sectionCount;

        if (!isHideBubble) {
            bubbleView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!isHideBubble) {
            locatePositionOnScreen();
        }
    }

    /**
     * In fact there two parts of the BubbleSeeBar, they are the BubbleView and the SeekBar.
     * <p>
     * The BubbleView is added to Window by the WindowManager, so the only connection between
     * BubbleView and SeekBar is their origin raw coordinates on the screen.
     * <p>
     * It's easy to compute the coordinates(bubbleCenterRawSolidX, bubbleCenterRawSolidY) of point
     * when the Progress equals the Min. Then compute the pixel length increment when the Progress is
     * changing, the result is bubbleCenterRawX. At last the WindowManager calls updateViewLayout()
     * to update the LayoutParameter.x of the BubbleView.
     * <p>
     * The bubble BubbleView is actually a view dynamically added by the WindowManager, so the only place to contact the SeekBar is that they are on the screen.
     * Absolute coordinates.
     * First calculate the center coordinates of the BubbleView when the progress progress is min (bubbleCenterRawSolidX, bubbleCenterRawSolidY),
     * Then calculate the abscissa bubbleCenterRawX incrementally according to the progress, and then dynamically set LayoutParameter.x to realize the bubble following sliding movement.
     */
    private void locatePositionOnScreen() {
        getLocationOnScreen(point);

        final ViewParent parent = getParent();
        if (parent != null && parent instanceof View && ((View) parent).getMeasuredWidth() > 0) {
            point[0] %= ((View) parent).getMeasuredWidth();
        }

        if (isRtl) {
            bubbleCenterRawSolidX = point[0] + right - bubbleView.getMeasuredWidth() / 2f;
        } else {
            bubbleCenterRawSolidX = point[0] + left - bubbleView.getMeasuredWidth() / 2f;
        }
        bubbleCenterRawX = calculateCenterRawXofBubbleView();
        bubbleCenterRawSolidY = point[1] - bubbleView.getMeasuredHeight();
//        bubbleCenterRawSolidY -= dp2px(2);

        final Context context = getContext();
        if (context instanceof Activity) {
            final Window window = ((Activity) context).getWindow();
            if (window != null) {
                int flags = window.getAttributes().flags;
                if ((flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
                    Resources res = Resources.getSystem();
                    int id = res.getIdentifier("status_bar_height", "dimen", "android");
                    bubbleCenterRawSolidY += res.getDimensionPixelSize(id);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xLeft = getPaddingLeft();
        float xRight = getMeasuredWidth() - getPaddingRight();
        final float yTop = getPaddingTop() + thumbRadiusOnDragging;

        // draw sectionText SIDES or BOTTOM_SIDES
        if (isShowSectionText) {
            paint.setColor(sectionTextColor);
            paint.setTextSize(sectionTextSize);
            paint.getTextBounds("0123456789", 0, "0123456789".length(), rectText); // compute solid height

            if (sectionTextPosition == TextPosition.SIDES) {
                float y_ = yTop + rectText.height() / 2f;

                String text = sectionTexts.get(0);
                paint.getTextBounds(text, 0, text.length(), rectText);
                canvas.drawText(text, xLeft + rectText.width() / 2f, y_, paint);
                xLeft += rectText.width() + textSpace;

                text = sectionTexts.get(sectionCount);
                paint.getTextBounds(text, 0, text.length(), rectText);
                canvas.drawText(text, xRight - (rectText.width() + 0.5f) / 2f, y_, paint);
                xRight -= (rectText.width() + textSpace);

            } else if (sectionTextPosition >= TextPosition.BOTTOM_SIDES) {
                float y_ = yTop + thumbRadiusOnDragging + textSpace;

                String text = sectionTexts.get(0);
                paint.getTextBounds(text, 0, text.length(), rectText);
                y_ += rectText.height();
                xLeft = left;
                if (sectionTextPosition == TextPosition.BOTTOM_SIDES) {
                    canvas.drawText(text, xLeft, y_, paint);
                }

                text = sectionTexts.get(sectionCount);
                paint.getTextBounds(text, 0, text.length(), rectText);
                xRight = right;
                if (sectionTextPosition == TextPosition.BOTTOM_SIDES) {
                    canvas.drawText(text, xRight, y_, paint);
                }
            }
        } else if (isShowThumbText && sectionTextPosition == NONE) {
            xLeft = left;
            xRight = right;
        }

        if ((!isShowSectionText && !isShowThumbText) || sectionTextPosition == TextPosition.SIDES) {
            xLeft += thumbRadiusOnDragging;
            xRight -= thumbRadiusOnDragging;
        }

        boolean isShowTextBelowSectionMark = isShowSectionText && sectionTextPosition ==
            TextPosition.BELOW_SECTION_MARK;

        // draw sectionMark & sectionText BELOW_SECTION_MARK
        if (isShowTextBelowSectionMark || isShowSectionMark) {
            paint.setTextSize(sectionTextSize);
            paint.getTextBounds("0123456789", 0, "0123456789".length(), rectText); // compute solid height

            float x_;
            float y_ = yTop + rectText.height() + thumbRadiusOnDragging + textSpace;
            float r = (thumbRadiusOnDragging - dp2px(2)) / 2f;
            float junction; // where secondTrack meets firstTrack
            if (isRtl) {
                junction = right - trackLength / delta * Math.abs(progress - min);
            } else {
                junction = left + trackLength / delta * Math.abs(progress - min);
            }

            for (int i = 0; i <= sectionCount; i++) {
                x_ = xLeft + i * sectionOffset;
                if (isRtl) {
                    paint.setColor(x_ <= junction ? trackColor : secondTrackColor);
                } else {
                    paint.setColor(x_ <= junction ? secondTrackColor : trackColor);
                }
                // sectionMark
                canvas.drawCircle(x_, yTop, r, paint);

                // sectionText belows section
                if (isShowTextBelowSectionMark) {
                    paint.setColor(sectionTextColor);
                    if (sectionTexts.get(i, null) != null) {
                        canvas.drawText(sectionTexts.get(i), x_, y_, paint);
                    }
                }
            }
        }

        if (!isThumbOnDragging || isAlwaysShowBubble) {
            if (isRtl) {
                thumbCenterX = xRight - trackLength / delta * (progress - min);
            } else {
                thumbCenterX = xLeft + trackLength / delta * (progress - min);
            }
        }

        // draw thumbText
        if (isShowThumbText && !isThumbOnDragging && isTouchToSeekAnimEnd) {
            paint.setColor(thumbTextColor);
            paint.setTextSize(thumbTextSize);
            paint.getTextBounds("0123456789", 0, "0123456789".length(), rectText); // compute solid height
            float y_ = yTop + rectText.height() + thumbRadiusOnDragging + textSpace;

            if (isFloatType || (isShowProgressInFloat && sectionTextPosition == TextPosition.BOTTOM_SIDES &&
                progress != min && progress != max)) {
                canvas.drawText(String.format("%s%s", String.valueOf(getProgressFloat()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)), thumbCenterX, y_, paint);
            } else {
                canvas.drawText(String.format("%s%s", String.valueOf(getProgress()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)), thumbCenterX, y_, paint);
            }
        }

        // draw track
        paint.setColor(secondTrackColor);
        paint.setStrokeWidth(secondTrackSize);
        if (isRtl) {
            canvas.drawLine(xRight, yTop, thumbCenterX, yTop, paint);
        } else {
            canvas.drawLine(xLeft, yTop, thumbCenterX, yTop, paint);
        }

        // draw second track
        paint.setColor(trackColor);
        paint.setStrokeWidth(trackSize);
        if (isRtl) {
            canvas.drawLine(thumbCenterX, yTop, xLeft, yTop, paint);
        } else {
            canvas.drawLine(thumbCenterX, yTop, xRight, yTop, paint);
        }

        // draw thumb
        paint.setColor(thumbColor);
        canvas.drawCircle(thumbCenterX, yTop, isThumbOnDragging ? thumbRadiusOnDragging : thumbRadius, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        post(this::requestLayout);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (isHideBubble || !isAlwaysShowBubble)
            return;

        if (visibility != VISIBLE) {
            hideBubble();
        } else {
            if (triggerBubbleShowing) {
                showBubble();
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideBubble();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                getParent().requestDisallowInterceptTouchEvent(true);

                isThumbOnDragging = isThumbTouched(event);
                if (isThumbOnDragging) {
                    if (isSeekBySection && !triggerSeekBySection) {
                        triggerSeekBySection = true;
                    }
                    if (isAlwaysShowBubble && !triggerBubbleShowing) {
                        triggerBubbleShowing = true;
                    }
                    if (!isHideBubble) {
                        showBubble();
                    }

                    invalidate();
                } else if (isTouchToSeek && isTrackTouched(event)) {
                    isThumbOnDragging = true;
                    if (isSeekBySection && !triggerSeekBySection) {
                        triggerSeekBySection = true;
                    }
                    if (isAlwaysShowBubble) {
                        hideBubble();
                        triggerBubbleShowing = true;
                    }

                    if (isSeekStepSection) {
                        thumbCenterX = preThumbCenterX = calThumbCxWhenSeekStepSection(event.getX());
                    } else {
                        thumbCenterX = event.getX();
                        if (thumbCenterX < left) {
                            thumbCenterX = left;
                        }
                        if (thumbCenterX > right) {
                            thumbCenterX = right;
                        }
                    }

                    progress = calculateProgress();

                    if (!isHideBubble) {
                        bubbleCenterRawX = calculateCenterRawXofBubbleView();
                        showBubble();
                    }

                    invalidate();
                }

                dx = thumbCenterX - event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                if (isThumbOnDragging) {
                    boolean flag = true;

                    if (isSeekStepSection) {
                        float x = calThumbCxWhenSeekStepSection(event.getX());
                        if (x != preThumbCenterX) {
                            thumbCenterX = preThumbCenterX = x;
                        } else {
                            flag = false;
                        }
                    } else {
                        thumbCenterX = event.getX() + dx;
                        if (thumbCenterX < left) {
                            thumbCenterX = left;
                        }
                        if (thumbCenterX > right) {
                            thumbCenterX = right;
                        }
                    }

                    if (flag) {
                        progress = calculateProgress();

                        if (!isHideBubble && bubbleView.getParent() != null) {
                            bubbleCenterRawX = calculateCenterRawXofBubbleView();
                            layoutParams.x = (int) (bubbleCenterRawX + 0.5f);
                            windowManager.updateViewLayout(bubbleView, layoutParams);
                            bubbleView.setProgressText(String.format("%s%s", isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)));
                        }

                        invalidate();

                        if (progressListener != null) {
                            progressListener.onProgressChanged(this, getProgress(), getProgressFloat(), true);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);

                if (isAutoAdjustSectionMark) {
                    if (isTouchToSeek) {
                        postDelayed(() -> {
                            isTouchToSeekAnimEnd = false;
                            autoAdjustSection();
                        }, animatioDuration);
                    } else {
                        autoAdjustSection();
                    }
                } else if (isThumbOnDragging || isTouchToSeek) {
                    if (isHideBubble) {
                        animate()
                            .setDuration(animatioDuration)
                            .setStartDelay(!isThumbOnDragging && isTouchToSeek ? 300 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isThumbOnDragging = false;
                                    invalidate();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    isThumbOnDragging = false;
                                    invalidate();
                                }
                            }).start();
                    } else {
                        postDelayed(() -> bubbleView.animate()
                            .alpha(isAlwaysShowBubble ? 1f : 0f)
                            .setDuration(animatioDuration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (!isAlwaysShowBubble) {
                                        hideBubble();
                                    }

                                    isThumbOnDragging = false;
                                    invalidate();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    if (!isAlwaysShowBubble) {
                                        hideBubble();
                                    }

                                    isThumbOnDragging = false;
                                    invalidate();
                                }
                            }).start(), animatioDuration);
                    }
                }

                if (progressListener != null) {
                    progressListener.onProgressChanged(this, getProgress(), getProgressFloat(), true);
                    progressListener.getProgressOnActionUp(this, getProgress(), getProgressFloat());
                }

                break;
        }

        return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event);
    }

    /**
     * Detect effective touch of thumb
     */
    private boolean isThumbTouched(MotionEvent event) {
        if (!isEnabled())
            return false;

        final float distance = trackLength / delta * (progress - min);
        final float x = isRtl ? right - distance : left + distance;
        final float y = getMeasuredHeight() / 2f;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y) <= (left + dp2px(8)) * (left + dp2px(8));
    }

    /**
     * Detect effective touch of track
     */
    private boolean isTrackTouched(MotionEvent event) {
        return isEnabled() && event.getX() >= getPaddingLeft() && event.getX() <= getMeasuredWidth() - getPaddingRight() && event.getY() >= getPaddingTop() && event.getY() <= getMeasuredHeight() - getPaddingBottom();
    }

    /**
     * If the thumb is being dragged, calculate the thumbCenterX when the seek_step_section is true.
     */
    private float calThumbCxWhenSeekStepSection(float touchedX) {
        if (touchedX <= left) {
            return left;
        }
        if (touchedX >= right) {
            return right;
        }

        int i;
        float x = 0;
        for (i = 0; i <= sectionCount; i++) {
            x = i * sectionOffset + left;
            if (x <= touchedX && touchedX - x <= sectionOffset) {
                break;
            }
        }

        if (touchedX - x <= sectionOffset / 2f) {
            return x;
        } else {
            return (i + 1) * sectionOffset + left;
        }
    }

    /**
     * Auto scroll to the nearest section mark
     */
    private void autoAdjustSection() {
        int i;
        float x = 0;
        for (i = 0; i <= sectionCount; i++) {
            x = i * sectionOffset + left;
            if (x <= thumbCenterX && thumbCenterX - x <= sectionOffset) {
                break;
            }
        }

        final BigDecimal bigDecimal = BigDecimal.valueOf(thumbCenterX);
        final float x_ = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        final boolean onSection = x_ == x; // At the section, without valueAnim, optimize performance

        final AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator valueAnim = null;
        if (!onSection) {
            if (thumbCenterX - x <= sectionOffset / 2f) {
                valueAnim = ValueAnimator.ofFloat(thumbCenterX, x);
            } else {
                valueAnim = ValueAnimator.ofFloat(thumbCenterX, (i + 1) * sectionOffset + left);
            }
            valueAnim.setInterpolator(new LinearInterpolator());
            valueAnim.addUpdateListener(animation -> {
                thumbCenterX = (float) animation.getAnimatedValue();
                progress = calculateProgress();

                if (!isHideBubble && bubbleView.getParent() != null) {
                    bubbleCenterRawX = calculateCenterRawXofBubbleView();
                    layoutParams.x = (int) (bubbleCenterRawX + 0.5f);
                    windowManager.updateViewLayout(bubbleView, layoutParams);
                    bubbleView.setProgressText(String.format("%s%s", isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)));
                }

                invalidate();

                if (progressListener != null) {
                    progressListener.onProgressChanged(DiscreteSeekBar.this, getProgress(), getProgressFloat(), true);
                }
            });
        }

        if (isHideBubble) {
            if (!onSection) {
                animatorSet.setDuration(animatioDuration).playTogether(valueAnim);
            }
        } else {
            final ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(bubbleView, View.ALPHA, isAlwaysShowBubble ? 1 : 0);
            if (onSection) {
                animatorSet.setDuration(animatioDuration).play(alphaAnim);
            } else {
                animatorSet.setDuration(animatioDuration).playTogether(valueAnim, alphaAnim);
            }
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isHideBubble && !isAlwaysShowBubble) {
                    hideBubble();
                }

                progress = calculateProgress();
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;

                invalidate();

                if (progressListener != null) {
                    progressListener.getProgressOnFinally(DiscreteSeekBar.this, getProgress(), getProgressFloat(), true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (!isHideBubble && !isAlwaysShowBubble) {
                    hideBubble();
                }

                progress = calculateProgress();
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;

                invalidate();
            }
        });

        animatorSet.start();
    }

    /**
     * Showing the Bubble depends the way that the WindowManager adds a Toast type view to the Window.
     * <p>
     * show bubbles
     * The principle is to dynamically add a BubbleView of the same type as Toast using WindowManager and remove it when it disappears.
     */
    private void showBubble() {
        if (bubbleView == null || bubbleView.getParent() != null) {
            return;
        }

        layoutParams.x = (int) (bubbleCenterRawX + 0.5f);
        layoutParams.y = (int) (bubbleCenterRawSolidY + 0.5f);

        bubbleView.setAlpha(0);
        bubbleView.setVisibility(VISIBLE);
        bubbleView.animate()
            .alpha(1f)
            .setDuration(isTouchToSeek ? 0 : animatioDuration)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    windowManager.addView(bubbleView, layoutParams);
                }
            })
            .start();
        bubbleView.setProgressText(String.format("%s%s", isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)));
    }

    /**
     * The WindowManager removes the BubbleView from the Window.
     */
    private void hideBubble() {
        if (bubbleView == null)
            return;

        bubbleView.setVisibility(GONE); // Anti-flicker
        if (bubbleView.getParent() != null) {
            windowManager.removeViewImmediate(bubbleView);
        }
    }

    private String float2String(float value) {
        return String.valueOf(formatFloat(value));
    }

    private float formatFloat(float value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private float calculateCenterRawXofBubbleView() {
        if (isRtl) {
            return bubbleCenterRawSolidX - trackLength * (progress - min) / delta;
        } else {
            return bubbleCenterRawSolidX + trackLength * (progress - min) / delta;
        }
    }

    private float calculateProgress() {
        if (isRtl) {
            return (right - thumbCenterX) * delta / trackLength + min;
        } else {
            return (thumbCenterX - left) * delta / trackLength + min;
        }
    }

    /////// Api begins /////////////////////////////////////////////////////////////////////////////

    /**
     * When BubbleSeekBar's parent view is scrollable, must listener to it's scrolling and call this
     * method to correct the offsets.
     */
    public void correctOffsetWhenContainerOnScrolling() {
        if (isHideBubble)
            return;

        locatePositionOnScreen();

        if (bubbleView.getParent() != null) {
            if (isAlwaysShowBubble) {
                layoutParams.y = (int) (bubbleCenterRawSolidY + 0.5f);
                windowManager.updateViewLayout(bubbleView, layoutParams);
            } else {
                postInvalidate();
            }
        }
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public void setProgress(float progress) {
        this.progress = progress;

        if (progressListener != null) {
            progressListener.onProgressChanged(this, getProgress(), getProgressFloat(), false);
            progressListener.getProgressOnFinally(this, getProgress(), getProgressFloat(), false);
        }
        if (!isHideBubble) {
            bubbleCenterRawX = calculateCenterRawXofBubbleView();
        }
        if (isAlwaysShowBubble) {
            hideBubble();

            postDelayed(() -> {
                showBubble();
                triggerBubbleShowing = true;
            }, alwaysShowBubbleDelay);
        }
        if (isSeekBySection) {
            triggerSeekBySection = false;
        }

        postInvalidate();
    }

    public int getProgress() {
        return Math.round(processProgress());
    }

    public float getProgressFloat() {
        return formatFloat(processProgress());
    }

    private float processProgress() {
        final float progress = this.progress;

        if (isSeekBySection && triggerSeekBySection) {
            float half = sectionValue / 2;

            if (isTouchToSeek) {
                if (progress == min || progress == max) {
                    return progress;
                }

                float secValue;
                for (int i = 0; i <= sectionCount; i++) {
                    secValue = i * sectionValue;
                    if (secValue < progress && secValue + sectionValue >= progress) {
                        if (secValue + half > progress) {
                            return secValue;
                        } else {
                            return secValue + sectionValue;
                        }
                    }
                }
            }

            if (progress >= previousSectionValue) { // increasing
                if (progress >= previousSectionValue + half) {
                    previousSectionValue += sectionValue;
                    return previousSectionValue;
                } else {
                    return previousSectionValue;
                }
            } else { // reducing
                if (progress >= previousSectionValue - half) {
                    return previousSectionValue;
                } else {
                    previousSectionValue -= sectionValue;
                    return previousSectionValue;
                }
            }
        }

        return progress;
    }

    public OnProgressChangedListener getOnProgressChangedListener() {
        return progressListener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        progressListener = onProgressChangedListener;
    }

    public void setTrackColor(@ColorInt int trackColor) {
        if (this.trackColor != trackColor) {
            this.trackColor = trackColor;
            invalidate();
        }
    }

    public void setSecondTrackColor(@ColorInt int secondTrackColor) {
        if (this.secondTrackColor != secondTrackColor) {
            this.secondTrackColor = secondTrackColor;
            invalidate();
        }
    }

    public void setThumbColor(@ColorInt int thumbColor) {
        if (this.thumbColor != thumbColor) {
            this.thumbColor = thumbColor;
            invalidate();
        }
    }

    public void setBubbleColor(@ColorInt int bubbleColor) {
        if (this.bubbleColor != bubbleColor) {
            this.bubbleColor = bubbleColor;
            if (bubbleView != null) {
                bubbleView.invalidate();
            }
        }
    }

    public void setCustomSectionTextArray(@NonNull CustomSectionTextArray customSectionTextArray) {
        sectionTexts = customSectionTextArray.onCustomize(sectionCount, sectionTexts);
        for (int i = 0; i <= sectionCount; i++) {
            if (sectionTexts.get(i) == null) {
                sectionTexts.put(i, "");
            }
        }

        isShowThumbText = false;
        requestLayout();
        invalidate();
    }
    /////// Api ends ///////////////////////////////////////////////////////////////////////////////

    void config(@NonNull final Builder builder) {
        min = builder.min;
        max = builder.max;
        progress = builder.progress;
        isFloatType = builder.floatType;
        trackSize = builder.trackSize;
        secondTrackSize = builder.secondTrackSize;
        thumbRadius = builder.thumbRadius;
        thumbRadiusOnDragging = builder.thumbRadiusOnDragging;
        trackColor = builder.trackColor;
        secondTrackColor = builder.secondTrackColor;
        thumbColor = builder.thumbColor;
        sectionCount = builder.sectionCount;
        isShowSectionMark = builder.showSectionMark;
        isAutoAdjustSectionMark = builder.autoAdjustSectionMark;
        isShowSectionText = builder.showSectionText;
        sectionTextSize = builder.sectionTextSize;
        sectionTextColor = builder.sectionTextColor;
        sectionTextPosition = builder.sectionTextPosition;
        sectionTextInterval = builder.sectionTextInterval;
        isShowThumbText = builder.showThumbText;
        thumbTextSize = builder.thumbTextSize;
        thumbTextColor = builder.thumbTextColor;
        isShowProgressInFloat = builder.showProgressInFloat;
        animatioDuration = builder.animDuration;
        isTouchToSeek = builder.touchToSeek;
        isSeekStepSection = builder.seekStepSection;
        isSeekBySection = builder.seekBySection;
        bubbleColor = builder.bubbleColor;
        bubbleTextSize = builder.bubbleTextSize;
        bubbleTextColor = builder.bubbleTextColor;
        isAlwaysShowBubble = builder.alwaysShowBubble;
        alwaysShowBubbleDelay = builder.alwaysShowBubbleDelay;
        isHideBubble = builder.hideBubble;
        isRtl = builder.rtl;

        initBuilderByPriority();
        calculateRadiusOfBubble();

        if (progressListener != null) {
            progressListener.onProgressChanged(this, getProgress(), getProgressFloat(), false);
            progressListener.getProgressOnFinally(this, getProgress(), getProgressFloat(), false);
        }

        this.builder = null;

        requestLayout();
    }

    public Builder newBuilder() {
        if (builder == null) {
            builder = new Builder(this);
        }

        builder.min = min;
        builder.max = max;
        builder.progress = progress;
        builder.floatType = isFloatType;
        builder.trackSize = trackSize;
        builder.secondTrackSize = secondTrackSize;
        builder.thumbRadius = thumbRadius;
        builder.thumbRadiusOnDragging = thumbRadiusOnDragging;
        builder.trackColor = trackColor;
        builder.secondTrackColor = secondTrackColor;
        builder.thumbColor = thumbColor;
        builder.sectionCount = sectionCount;
        builder.showSectionMark = isShowSectionMark;
        builder.autoAdjustSectionMark = isAutoAdjustSectionMark;
        builder.showSectionText = isShowSectionText;
        builder.sectionTextSize = sectionTextSize;
        builder.sectionTextColor = sectionTextColor;
        builder.sectionTextPosition = sectionTextPosition;
        builder.sectionTextInterval = sectionTextInterval;
        builder.showThumbText = isShowThumbText;
        builder.thumbTextSize = thumbTextSize;
        builder.thumbTextColor = thumbTextColor;
        builder.showProgressInFloat = isShowProgressInFloat;
        builder.animDuration = animatioDuration;
        builder.touchToSeek = isTouchToSeek;
        builder.seekStepSection = isSeekStepSection;
        builder.seekBySection = isSeekBySection;
        builder.bubbleColor = bubbleColor;
        builder.bubbleTextSize = bubbleTextSize;
        builder.bubbleTextColor = bubbleTextColor;
        builder.alwaysShowBubble = isAlwaysShowBubble;
        builder.alwaysShowBubbleDelay = alwaysShowBubbleDelay;
        builder.hideBubble = isHideBubble;
        builder.rtl = isRtl;

        return builder;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("save_instance", super.onSaveInstanceState());
        bundle.putFloat("progress", progress);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            progress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("save_instance"));

            if (bubbleView != null) {
                bubbleView.setProgressText(String.format("%s%s", isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress()), TextUtils.isEmpty(unit) ? "" : String.format(" %s", unit)));
            }

            setProgress(progress);

            return;
        }

        super.onRestoreInstanceState(state);
    }

    /**
     * Listen to progress onChanged, onActionUp, onFinally
     */
    public interface OnProgressChangedListener {

        void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, float progressFloat, boolean fromUser);

        void getProgressOnActionUp(DiscreteSeekBar discreteSeekBar, int progress, float progressFloat);

        void getProgressOnFinally(DiscreteSeekBar discreteSeekBar, int progress, float progressFloat, boolean fromUser);
    }

    /**
     * Listener adapter
     * <br/>
     * usage like {@link AnimatorListenerAdapter}
     */
    public static abstract class OnProgressChangedListenerAdapter implements OnProgressChangedListener {

        @Override
        public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, float progressFloat, boolean fromUser) {
        }

        @Override
        public void getProgressOnActionUp(DiscreteSeekBar discreteSeekBar, int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnFinally(DiscreteSeekBar discreteSeekBar, int progress, float progressFloat, boolean fromUser) {
        }
    }

    /**
     * Customize the section texts under the track according to your demands by
     * call {@link #setCustomSectionTextArray(CustomSectionTextArray)}.
     */
    public interface CustomSectionTextArray {
        /**
         * <p>
         * Customization goes here.
         * </p>
         * For example:
         * <pre> public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
         *     array.clear();
         *
         *     array.put(0, "worst");
         *     array.put(4, "bad");
         *     array.put(6, "ok");
         *     array.put(8, "good");
         *     array.put(9, "great");
         *     array.put(10, "excellent");
         * }</pre>
         *
         * @param sectionCount The section count of the {@code BubbleSeekBar}.
         * @param array        The section texts array which had been initialized already. Customize
         *                     the section text by changing one element's value of the SparseArray.
         *                     The index key ∈[0, sectionCount].
         * @return The customized section texts array. Can not be {@code null}.
         */
        @NonNull
        SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array);
    }

    class BubbleView extends View {

        private Paint bubblePaint;
        private Path bubblePath;
        private RectF bubbleRectF;
        private Rect bubbleRect;
        private String bubbleProgressText = "";

        BubbleView(Context context) {
            this(context, null);
        }

        BubbleView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            bubblePaint = new Paint();
            bubblePaint.setAntiAlias(true);
            bubblePaint.setTextAlign(Paint.Align.CENTER);

            bubblePath = new Path();
            bubbleRectF = new RectF();
            bubbleRect = new Rect();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension(
                3 * bubbleRadius,
                3 * bubbleRadius
            );

            bubbleRectF.set(
                getMeasuredWidth() / 2f - bubbleRadius,
                0,
                getMeasuredWidth() / 2f + bubbleRadius,
                2 * bubbleRadius
            );
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            bubblePath.reset();
            final float x0 = getMeasuredWidth() / 2f;
            final float y0 = getMeasuredHeight() - bubbleRadius / 3f;
            bubblePath.moveTo(x0, y0);
            final float x1 = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * bubbleRadius);
            final float y1 = 3 / 2f * bubbleRadius;
            bubblePath.quadTo(
                x1 - dp2px(2),
                y1 - dp2px(2),
                x1,
                y1
            );
            bubblePath.arcTo(bubbleRectF, 150, 240);

            final float x2 = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * bubbleRadius);
            bubblePath.quadTo(
                x2 + dp2px(2), y1 - dp2px(2),
                x0, y0
            );
            bubblePath.close();

            bubblePaint.setColor(bubbleColor);
            canvas.drawPath(bubblePath, bubblePaint);

            bubblePaint.setTextSize(bubbleTextSize);
            bubblePaint.setColor(bubbleTextColor);
            bubblePaint.getTextBounds(bubbleProgressText, 0, bubbleProgressText.length(), bubbleRect);
            final Paint.FontMetrics fontMetrics = bubblePaint.getFontMetrics();
            final float baseline = bubbleRadius + (fontMetrics.descent - fontMetrics.ascent) / 2f - fontMetrics.descent;
            canvas.drawText(bubbleProgressText, getMeasuredWidth() / 2f, baseline, bubblePaint);
        }

        void setProgressText(@NonNull final String progressText) {
            if (progressText != null && !bubbleProgressText.equals(progressText)) {
                bubbleProgressText = progressText;
                invalidate();
            }
        }
    }

    public static class Builder {
        String unit;
        float min;
        float max;
        float progress;
        boolean floatType;
        int trackSize;
        int secondTrackSize;
        int thumbRadius;
        int thumbRadiusOnDragging;
        int trackColor;
        int secondTrackColor;
        int thumbColor;
        int sectionCount;
        boolean showSectionMark;
        boolean autoAdjustSectionMark;
        boolean showSectionText;
        int sectionTextSize;
        int sectionTextColor;
        @DiscreteSeekBar.TextPosition
        int sectionTextPosition;
        int sectionTextInterval;
        boolean showThumbText;
        int thumbTextSize;
        int thumbTextColor;
        boolean showProgressInFloat;
        long animDuration;
        boolean touchToSeek;
        boolean seekStepSection;
        boolean seekBySection;
        int bubbleColor;
        int bubbleTextSize;
        int bubbleTextColor;
        boolean alwaysShowBubble;
        long alwaysShowBubbleDelay;
        boolean hideBubble;
        boolean rtl;

        private DiscreteSeekBar discreteSeekBar;

        Builder(DiscreteSeekBar discreteSeekBar) {
            this.discreteSeekBar = discreteSeekBar;
        }

        public void build() {
            discreteSeekBar.config(this);
        }

        public Builder unit(@NonNull final String unit) {
            this.unit = unit;
            return this;
        }

        public Builder min(final float min) {
            this.min = min;
            this.progress = min;
            return this;
        }

        public Builder max(final float max) {
            this.max = max;
            return this;
        }

        public Builder progress(final float progress) {
            this.progress = progress;
            return this;
        }

        public Builder floatType() {
            this.floatType = true;
            return this;
        }

        public Builder trackSize(final int dp) {
            this.trackSize = dp2px(dp);
            return this;
        }

        public Builder secondTrackSize(final int dp) {
            this.secondTrackSize = dp2px(dp);
            return this;
        }

        public Builder thumbRadius(final int dp) {
            this.thumbRadius = dp2px(dp);
            return this;
        }

        public Builder thumbRadiusOnDragging(final int dp) {
            this.thumbRadiusOnDragging = dp2px(dp);
            return this;
        }

        public Builder trackColor(@ColorInt int color) {
            this.trackColor = color;
            this.sectionTextColor = color;
            return this;
        }

        public Builder secondTrackColor(@ColorInt int color) {
            this.secondTrackColor = color;
            this.thumbColor = color;
            this.thumbTextColor = color;
            this.bubbleColor = color;
            return this;
        }

        public Builder thumbColor(@ColorInt int color) {
            this.thumbColor = color;
            return this;
        }

        public Builder sectionCount(@IntRange(from = 1) int count) {
            this.sectionCount = count;
            return this;
        }

        public Builder showSectionMark() {
            this.showSectionMark = true;
            return this;
        }

        public Builder autoAdjustSectionMark() {
            this.autoAdjustSectionMark = true;
            return this;
        }

        public Builder showSectionText() {
            this.showSectionText = true;
            return this;
        }

        public Builder sectionTextSize(final int sp) {
            this.sectionTextSize = sp2px(sp);
            return this;
        }

        public Builder sectionTextColor(@ColorInt int color) {
            this.sectionTextColor = color;
            return this;
        }

        public Builder sectionTextPosition(@DiscreteSeekBar.TextPosition final int position) {
            this.sectionTextPosition = position;
            return this;
        }

        public Builder sectionTextInterval(@IntRange(from = 1) final int interval) {
            this.sectionTextInterval = interval;
            return this;
        }

        public Builder showThumbText() {
            this.showThumbText = true;
            return this;
        }

        public Builder thumbTextSize(final int sp) {
            this.thumbTextSize = sp2px(sp);
            return this;
        }

        public Builder thumbTextColor(@ColorInt final int color) {
            thumbTextColor = color;
            return this;
        }

        public Builder showProgressInFloat() {
            this.showProgressInFloat = true;
            return this;
        }

        public Builder animDuration(final long duration) {
            animDuration = duration;
            return this;
        }

        public Builder touchToSeek() {
            this.touchToSeek = true;
            return this;
        }

        public Builder seekStepSection() {
            this.seekStepSection = true;
            return this;
        }

        public Builder seekBySection() {
            this.seekBySection = true;
            return this;
        }

        public Builder bubbleColor(@ColorInt final int color) {
            this.bubbleColor = color;
            return this;
        }

        public Builder bubbleTextSize(final int sp) {
            this.bubbleTextSize = sp2px(sp);
            return this;
        }

        public Builder bubbleTextColor(@ColorInt final int color) {
            this.bubbleTextColor = color;
            return this;
        }

        public Builder alwaysShowBubble() {
            this.alwaysShowBubble = true;
            return this;
        }

        public Builder alwaysShowBubbleDelay(final long delay) {
            alwaysShowBubbleDelay = delay;
            return this;
        }

        public Builder hideBubble() {
            this.hideBubble = true;
            return this;
        }

        public Builder rtl(final boolean rtl) {
            this.rtl = rtl;
            return this;
        }


        public String getUnit() {
            return unit;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        public float getProgress() {
            return progress;
        }

        public boolean isFloatType() {
            return floatType;
        }

        public int getTrackSize() {
            return trackSize;
        }

        public int getSecondTrackSize() {
            return secondTrackSize;
        }

        public int getThumbRadius() {
            return thumbRadius;
        }

        public int getThumbRadiusOnDragging() {
            return thumbRadiusOnDragging;
        }

        public int getTrackColor() {
            return trackColor;
        }

        public int getSecondTrackColor() {
            return secondTrackColor;
        }

        public int getThumbColor() {
            return thumbColor;
        }

        public int getSectionCount() {
            return sectionCount;
        }

        public boolean isShowSectionMark() {
            return showSectionMark;
        }

        public boolean isAutoAdjustSectionMark() {
            return autoAdjustSectionMark;
        }

        public boolean isShowSectionText() {
            return showSectionText;
        }

        public int getSectionTextSize() {
            return sectionTextSize;
        }

        public int getSectionTextColor() {
            return sectionTextColor;
        }

        public int getSectionTextPosition() {
            return sectionTextPosition;
        }

        public int getSectionTextInterval() {
            return sectionTextInterval;
        }

        public boolean isShowThumbText() {
            return showThumbText;
        }

        public int getThumbTextSize() {
            return thumbTextSize;
        }

        public int getThumbTextColor() {
            return thumbTextColor;
        }

        public boolean isShowProgressInFloat() {
            return showProgressInFloat;
        }

        public long getAnimDuration() {
            return animDuration;
        }

        public boolean isTouchToSeek() {
            return touchToSeek;
        }

        public boolean isSeekStepSection() {
            return seekStepSection;
        }

        public boolean isSeekBySection() {
            return seekBySection;
        }

        public int getBubbleColor() {
            return bubbleColor;
        }

        public int getBubbleTextSize() {
            return bubbleTextSize;
        }

        public int getBubbleTextColor() {
            return bubbleTextColor;
        }

        public boolean isAlwaysShowBubble() {
            return alwaysShowBubble;
        }

        public long getAlwaysShowBubbleDelay() {
            return alwaysShowBubbleDelay;
        }

        public boolean isHideBubble() {
            return hideBubble;
        }

        public boolean isRtl() {
            return rtl;
        }

        static int dp2px(final int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
        }

        static int sp2px(final int sp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
        }
    }
}

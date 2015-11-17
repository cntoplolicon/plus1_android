package com.oneplusapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.oneplusapp.R;

/**
 * Created by jiewei on 11/2/15.
 */
public class AdjustableImageView extends ImageView {
    private boolean mAdjustableImageView;
    private float min_height_width_ratio;
    private float max_height_width_ratio;

    public AdjustableImageView(Context context) {
        super(context);
    }

    public AdjustableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AdjustableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        mAdjustableImageView = adjustViewBounds;
        super.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable mDrawable = getDrawable();
        if (mDrawable == null || !mAdjustableImageView) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int mDrawableWidth = mDrawable.getIntrinsicWidth();
        int mDrawableHeight = mDrawable.getIntrinsicHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // Fixed Width & Adjustable Height
            float hwRatio = mDrawableHeight * 1.0f / mDrawableWidth;
            float height = widthSize * hwRatio;
            if (hwRatio > max_height_width_ratio) {
                height = widthSize * max_height_width_ratio;
            } else if (hwRatio < min_height_width_ratio) {
                height = widthSize * min_height_width_ratio;
            }
            int heightSize = (int)height;
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdjustableImageView);
        min_height_width_ratio = a.getFloat(R.styleable.AdjustableImageView_min_height_width_ratio, Float.MIN_VALUE);
        max_height_width_ratio = a.getFloat(R.styleable.AdjustableImageView_max_height_width_ratio, Float.MAX_VALUE);
    }
}

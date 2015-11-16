package com.oneplusapp.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jiewei on 11/2/15.
 */
public class RecommendItemImageView extends ImageView {
    private boolean mAdjustableImageView;

    public RecommendItemImageView(Context context) {
        super(context);
    }

    public RecommendItemImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecommendItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
            int width = widthSize;
            int height = width * mDrawableHeight / mDrawableWidth;

            if (mAdjustableImageView && height / 2 >= width) {
                height = (int) (width * 2.0);
            } else if (mAdjustableImageView && height <= width / 2) {
                height = (int) (width / 2.0);
            }
            setMeasuredDimension(Math.min(width, widthSize), height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

package com.oneplusapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class PostGridViewItemLayout extends FrameLayout {

    public PostGridViewItemLayout(Context context) {
        super(context);
    }

    public PostGridViewItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostGridViewItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMeasureMode == MeasureSpec.EXACTLY && heightMeasureMode != MeasureSpec.EXACTLY) {
            int desiredHeight = resolveSize(widthMeasureSize, heightMeasureSpec);
            setMeasuredDimension(widthMeasureSize, desiredHeight);
            measureChildrenByDesiredSize(widthMeasureSize, desiredHeight);
        } else if (heightMeasureMode == MeasureSpec.EXACTLY && widthMeasureMode != MeasureSpec.EXACTLY) {
            int desiredWidth = resolveSize(heightMeasureSize, widthMeasureSpec);
            setMeasuredDimension(desiredWidth, heightMeasureSize);
            measureChildrenByDesiredSize(desiredWidth, heightMeasureSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureChildrenByDesiredSize(int desiredWidth, int desiredHeight) {
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }
}

package com.oneplusapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SquareItemTextView extends TextView {
    public SquareItemTextView(Context context) {
        super(context);
    }

    public SquareItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = resolveSize(widthSize, heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, desiredHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

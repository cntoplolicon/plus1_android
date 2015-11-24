package com.oneplusapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class DetachableImageView extends ImageView {

    private List<Runnable> postQueue = new ArrayList<>();
    private boolean attached;

    public DetachableImageView(Context context) {
        super(context);
    }

    public DetachableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetachableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        attached = true;
        super.onAttachedToWindow();
        for (Runnable runnable : postQueue) {
            super.post(runnable);
        }
        postQueue.clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        attached = false;
        super.onDetachedFromWindow();
    }

    @Override
    public boolean post(Runnable action) {
        if (attached) {
            return super.post(action);
        } else {
            postQueue.add(action);
        }
        return true;
    }

    @Override
    public boolean isAttachedToWindow() {
        return attached;
    }
}
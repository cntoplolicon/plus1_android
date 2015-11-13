package com.oneplusapp.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.oneplusapp.R;

public class DraggableStackView extends ViewGroup {

    // affect the touch slop and  the minimum drag slot and settling duration
    private static int DRAG_RANGE_REDUCE_FACTOR = 10;
    // affect the minimum offset to drag a view off the screen
    private static int DRAG_OFF_OFFSET_FACTOR = 3;

    private final ViewDragHelper dragHelper;

    private View headerView;
    private View footerView;

    private View stackTopView;
    private View stackNextView;

    private int offset;
    private int dragRange;
    private int settleStart, settleEnd;
    private boolean settling;
    private int minimumDragOffOffset;

    private BaseAdapter adapter;
    private OnViewReleasedListener onViewReleasedListener;

    public DraggableStackView(Context context) {
        this(context, null);
    }

    public DraggableStackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    private void syncStackViews() {
        if (stackTopView != null && stackTopView.getParent() == this) {
            removeView(stackTopView);
        }
        if (stackNextView != null && stackNextView.getParent() == this) {
            removeView(stackNextView);
        }

        stackTopView = adapter.getView(0, stackNextView, this);
        if (stackTopView != null) {
            addView(stackTopView, 0);
        }

        stackNextView = adapter.getView(1, null, this);
        if (stackNextView != null) {
            addView(stackNextView, 0);
        }
    }

    private void recycleContentViewBitmap(View contentView) {
        if (contentView == null || contentView.getParent() != null) {
            return;
        }
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_image);
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            imageView.setImageBitmap(null);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.getBitmap().recycle();
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                syncStackViews();
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        headerView = findViewById(R.id.top_view);
        footerView = findViewById(R.id.bottom_view);
        minimumDragOffOffset = Math.max(getDesiredHeight(headerView), minimumDragOffOffset);
        minimumDragOffOffset = Math.max(getDesiredHeight(footerView), minimumDragOffOffset);
        minimumDragOffOffset = Math.max(dragHelper.getTouchSlop(), minimumDragOffOffset * DRAG_OFF_OFFSET_FACTOR);
    }

    private int getDesiredHeight(View view) {
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }


    private void onCapturedViewSettled() {
        if (stackTopView != null) {
            removeView(stackTopView);
            if (onViewReleasedListener != null) {
                onViewReleasedListener.onReleasedViewSettled(stackTopView, offset);
            }
            recycleContentViewBitmap(stackTopView);
        }
        offset = 0;
    }

    @Override
    public void computeScroll() {
        Log.d("computeScroll()", "" + settling);
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            Log.d("computeScroll()", "continueSettling");
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (settling) {
            settling = false;
            onCapturedViewSettled();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("onInterceptTouchEvent", "" + dragHelper.shouldInterceptTouchEvent(ev));
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        dragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("onMeasure()", "" + settling);

        int topViewHeight = computeHeaderViewHeight();
        measureExactHeight(headerView, topViewHeight, widthMeasureSpec);

        int bottomViewHeight = computeFooterViewHeight();
        measureExactHeight(footerView, bottomViewHeight, widthMeasureSpec);

        if (stackTopView != null) {
            stackTopView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (stackNextView != null) {
            stackNextView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int computeHeaderViewHeight() {
        if (settling && settleEnd > settleStart) {
            return (int) (1.0f * (offset - settleEnd) / (settleStart - settleEnd) * settleStart);
        }
        return offset > 0 ? offset : 0;
    }

    private int computeFooterViewHeight() {
        if (settling && settleEnd < settleStart) {
            return (int) (1.0f * (offset - settleEnd) / (settleEnd - settleStart) * settleStart);
        }
        return offset < 0 ? -offset : 0;
    }

    private void measureExactHeight(View view, int height, int widthMeasureSpec) {
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("onLayout()", l + " " + t + " " + r + " " + b + " " + settling);
        dragRange = getHeight();

        int headerViewHeight = computeHeaderViewHeight();
        headerView.layout(l, t, r, t + headerViewHeight);

        int footerViewHeight = computeFooterViewHeight();
        footerView.layout(l, b - footerViewHeight, r, b);

        if (stackNextView != null) {
            stackNextView.layout(l, t, r, b);
        }
        if (stackTopView != null) {
            if (headerViewHeight > 0) {
                stackTopView.layout(l, t + headerViewHeight, r, t + headerViewHeight + stackTopView.getMeasuredHeight());
            } else {
                stackTopView.layout(l, b - footerViewHeight - stackTopView.getMeasuredHeight(), r, b - footerViewHeight);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (stackTopView != null) {
            this.removeView(stackTopView);
        }
        if (stackNextView != null) {
            this.removeView(stackNextView);
        }
    }

    public void setOnViewReleasedListener(OnViewReleasedListener listener) {
        this.onViewReleasedListener = listener;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == stackTopView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d("onViewPositionChanged()", left + " " + top + " " + dx + " " + dy);
            offset = top;
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (Math.abs(offset) < minimumDragOffOffset) {
                dragHelper.settleCapturedViewAt(releasedChild.getLeft(), 0);
                invalidate();
                return;
            }

            if (onViewReleasedListener != null) {
                onViewReleasedListener.onViewReleased(releasedChild, offset);
            }

            settleStart = offset;
            settleEnd = offset > 0 ? dragRange : -dragRange;
            settling = dragHelper.settleCapturedViewAt(releasedChild.getLeft(), settleEnd);
            if (!settling) {
                onCapturedViewSettled();
            }
            invalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange / DRAG_RANGE_REDUCE_FACTOR;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Math.min(Math.max(top, -dragRange), dragRange);
        }
    }

    public interface OnViewReleasedListener {
        void onViewReleased(View view, int offset);

        void onReleasedViewSettled(View view, int offset);
    }
}

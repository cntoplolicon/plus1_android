package com.oneplusapp.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DraggableStackView extends ViewGroup {

    // affect the touch slop and  the minimum drag slot and settling duration
    private static int DRAG_RANGE_REDUCE_FACTOR = 10;

    private final ViewDragHelper dragHelper;

    private View headerView;
    private View footerView;

    private View stackTopView;
    private View stackNextView;

    private boolean needSyncViewsOnReleaseSettled;
    private int position;
    private int dragOffsetLimit;
    private int offset;
    private int dragRange;
    private int settleStart, settleEnd;
    private boolean settling;

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
        dragOffsetLimit = dragHelper.getTouchSlop();
    }

    private void syncStackViews() {
        if (stackTopView != null && stackTopView.getParent() == this) {
            removeView(stackTopView);
        }
        if (stackNextView != null && stackNextView.getParent() == this) {
            removeView(stackNextView);
        }

        if (position < adapter.getCount()) {
            stackTopView = adapter.getView(position, stackTopView, this);
            addView(stackTopView, 0);
        } else {
            stackTopView = null;
        }
        if (position + 1 < adapter.getCount()) {
            stackNextView = adapter.getView(position + 1, stackNextView, this);
            addView(stackNextView, 0);
        } else {
            stackNextView = null;
        }

        needSyncViewsOnReleaseSettled = false;
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

    public void setHeaderView(View view) {
        if (headerView != null && headerView.getParent() == this) {
            removeView(headerView);
        }
        headerView = view;
        if (headerView != null) {
            addView(headerView);
        }
    }

    public void setFooterView(View view) {
        if (footerView != null && footerView.getParent() == this) {
            removeView(footerView);
        }
        footerView = view;
        if (footerView != null) {
            addView(footerView);
        }
    }

    public void setDragOffsetLimit(int limit) {
        dragOffsetLimit = Math.max(limit, dragHelper.getTouchSlop());
    }

    public void resetPosition() {
        position = 0;
    }

    private void onCapturedViewSettled() {
        ++position;
        needSyncViewsOnReleaseSettled = true;
        if (stackNextView != null) {
            View tempView = stackNextView;
            stackNextView = stackTopView;
            stackTopView = tempView;
        }
        if (stackTopView != null && onViewReleasedListener != null) {
            onViewReleasedListener.onReleasedViewSettled(stackTopView, offset);
        }
        offset = 0;
        if (needSyncViewsOnReleaseSettled) {
            syncStackViews();
        }
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
        int unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        if (headerView != null) {
            headerView.measure(widthMeasureSpec, unspecifiedSpec);
            int headerViewOffset = computeHeaderViewOffset();
            if (headerViewOffset > headerView.getMeasuredHeight()) {
                int heightSpec = MeasureSpec.makeMeasureSpec(headerViewOffset, MeasureSpec.EXACTLY);
                headerView.measure(widthMeasureSpec, heightSpec);
            }
        }
        if (footerView != null) {
            footerView.measure(widthMeasureSpec, unspecifiedSpec);
            int footerViewOffset = computeFooterViewOffset();
            if (footerViewOffset > footerView.getMeasuredHeight()) {
                int heightSpec = MeasureSpec.makeMeasureSpec(footerViewOffset, MeasureSpec.EXACTLY);
                footerView.measure(widthMeasureSpec, heightSpec);
            }
        }

        if (stackTopView != null) {
            stackTopView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (stackNextView != null) {
            stackNextView.measure(widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int computeHeaderViewOffset() {
        if (settling && settleEnd > settleStart) {
            return (int) (1.0f * (offset - settleEnd) / (settleStart - settleEnd) * settleStart);
        }
        return offset > 0 ? offset : 0;
    }

    private int computeFooterViewOffset() {
        if (settling && settleEnd < settleStart) {
            return (int) (1.0f * (offset - settleEnd) / (settleEnd - settleStart) * settleStart);
        }
        return offset < 0 ? -offset : 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("onLayout()", l + " " + t + " " + r + " " + b + " " + settling);
        dragRange = getHeight();

        if (stackNextView != null) {
            stackNextView.layout(l, t, r, b);
        }
        if (stackTopView != null) {
            stackTopView.layout(l, l + offset, r, l + offset + stackTopView.getMeasuredHeight());
        }
        if (headerView != null) {
            int headerViewOffset = computeHeaderViewOffset();
            headerView.layout(l, t + headerViewOffset - headerView.getMeasuredHeight(), r, t + headerViewOffset);
        }
        if (footerView != null) {
            int footerViewOffset = computeFooterViewOffset();
            footerView.layout(l, b - footerViewOffset, r, b - footerViewOffset + footerView.getMeasuredHeight());
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
            if (Math.abs(offset) < dragOffsetLimit) {
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

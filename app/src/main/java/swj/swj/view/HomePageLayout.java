package swj.swj.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import swj.swj.R;
import swj.swj.adapter.InfectionsAdapter;

/**
 * Created by cntoplolicon on 9/21/15.
 */
public class HomePageLayout extends ViewGroup {

    // affect the touch slop and  the minimum drag slot and settling duration
    private static int DRAG_RANGE_REDUCE_FACTOR = 10;

    private final ViewDragHelper dragHelper;

    private View topView;
    private View currentContentView;
    private View nextContentView;
    private View bottomView;

    private int offset;
    private int dragRange;
    private int settleStart, settleEnd;
    private boolean settling;
    private int minimumSuggestedHeight;

    private InfectionsAdapter adapter;
    private Callback callback;

    public HomePageLayout(Context context) {
        this(context, null);
    }

    public HomePageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void syncContentViews() {
        View oldCurrentContentView = currentContentView;
        if (currentContentView != null) {
            this.removeView(currentContentView);
        }
        if (nextContentView != null) {
            this.removeView(nextContentView);
        }

        currentContentView = adapter.getViewAt(0, nextContentView);
        if (currentContentView != null) {
            this.addView(currentContentView, 0);
            if (callback != null) {
                callback.onViewAdded(currentContentView);
            }
        }

        nextContentView = adapter.getViewAt(1, oldCurrentContentView);
        if (nextContentView != null) {
            this.addView(nextContentView, 0);
            if (callback != null) {
                callback.onViewAdded(nextContentView);
            }
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

    public void setAdapter(InfectionsAdapter adapter) {
        if (this.adapter != null) {
            throw new IllegalStateException("adapter already set");
        }
        this.adapter = adapter;
        syncContentViews();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        topView = findViewById(R.id.top_view);
        bottomView = findViewById(R.id.bottom_view);
        minimumSuggestedHeight = Math.max(getDesiredHeight(topView), minimumSuggestedHeight);
        minimumSuggestedHeight = Math.max(getDesiredHeight(bottomView), minimumSuggestedHeight);
        minimumSuggestedHeight = Math.max(dragHelper.getTouchSlop(), minimumSuggestedHeight * 3);
    }

    private int getDesiredHeight(View view) {
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    public HomePageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == currentContentView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d("onViewPositionChanged()", left + " " + top + " " + dx + " " + dy);
            offset = top;
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (Math.abs(offset) < minimumSuggestedHeight) {
                dragHelper.settleCapturedViewAt(releasedChild.getLeft(), 0);
                invalidate();
                return;
            }
            settleStart = offset;
            settleEnd = offset > 0 ? dragRange : -dragRange;
            settling = dragHelper.settleCapturedViewAt(releasedChild.getLeft(), settleEnd);
            if (callback != null) {
                callback.onViewReleased(releasedChild, offset);
            }
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

    private void onCapturedViewSettled() {
        if (currentContentView != null) {
            adapter.remove(currentContentView);
            recycleContentViewBitmap(currentContentView);
        }
        offset = 0;
        syncContentViews();
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

        int topViewHeight = computeTopViewHeight();
        measureExactHeight(topView, topViewHeight, widthMeasureSpec);

        int bottomViewHeight = computeBottomViewHeight();
        measureExactHeight(bottomView, bottomViewHeight, widthMeasureSpec);

        if (currentContentView != null) {
            currentContentView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (nextContentView != null) {
            nextContentView.measure(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int computeTopViewHeight() {
        Log.d("computeTopViewHeight", "" + settling + " " + offset);
        if (settling && settleEnd > settleStart) {
            return (int) (1.0f * (offset - settleEnd) / (settleStart - settleEnd) * settleStart);
        }
        return offset > 0 ? offset : 0;
    }

    private int computeBottomViewHeight() {
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

        int topViewHeight = computeTopViewHeight();
        topView.layout(l, t, r, t + topViewHeight);

        int bottomViewHeight = computeBottomViewHeight();
        bottomView.layout(l, b - bottomViewHeight, r, b);

        if (nextContentView != null) {
            nextContentView.layout(l, t, r, b);
        }
        if (currentContentView != null) {
            if (topViewHeight > 0) {
                currentContentView.layout(l, t + topViewHeight, r, t + topViewHeight + currentContentView.getMeasuredHeight());
            } else {
                currentContentView.layout(l, b - bottomViewHeight - currentContentView.getMeasuredHeight(), r, b - bottomViewHeight);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (currentContentView != null) {
            this.removeView(currentContentView);
        }
        if (nextContentView != null) {
            this.removeView(nextContentView);
        }
    }

    public interface Callback {
        void onViewAdded(View view);

        void onViewReleased(View view, int offset);
    }
}

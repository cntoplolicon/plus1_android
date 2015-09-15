package swj.swj.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import swj.swj.adapter.SlidingViewAdapter;


public class MySlidingView extends FrameLayout {
    // 适配器
    private SlidingViewAdapter mAdapter;
    // Item类型个数，从1开始
    // private int type = 1;
    // Item布局资源文件
    // private int[] layoutResource = new int[type];
    // Item个数
    private int count = 0;
    // 当前Item的编号
    private int position = 0;
    // ViewDragHelper 回调类，控制滑动效果
    private SlidingViewCallBack callBack = new SlidingViewCallBack();
    // 防止误触
    private boolean isTouchable = true;

    private View convertView;
    private View displayView;
    private View changeView;
    private View nextView;
    private View movingView;

    private int layoutHeight = 0;
    private int layoutWidth = 0;
    private ViewDragHelper viewDragHelper;

    public MySlidingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public MySlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MySlidingView(Context context) {
        super(context);
        initView();
    }

    private void initView() {

        viewDragHelper = ViewDragHelper.create(this, callBack);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        // super.onLayout(changed, left, top, right, bottom);

        // 自己布局，按照自己的要求
        if (mAdapter != null) {

            // 获取宽高
            if (displayView != null && changeView == null) {

                // 首次进入，初始化对应的布局文件与长、宽获取
                layoutHeight = displayView.getMeasuredHeight();
                layoutWidth = displayView.getMeasuredWidth();

                displayView.layout(0, 0, layoutWidth, layoutHeight);
                convertView.layout(0, 0, layoutWidth, layoutHeight);
            }

            if (changeView != null) {

                Log.d("onLayout", changeView.toString());
                changeView.layout(0, 0, layoutWidth, layoutHeight);

            }

        } else {

            super.onLayout(changed, left, top, right, bottom);

        }
    }

    private float downX;
    private float downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isTouchable) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    downX = event.getX();
                    downY = event.getY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    float moveX = event.getX();
                    float moveY = event.getY();

                    float offsetX = moveX - downX;
                    float offsetY = moveY - downY;

                    if (Math.abs(offsetX) < Math.abs(offsetY)) {
                        // 纵向移动，请求不拦截
                        requestDisallowInterceptTouchEvent(true);
                    }

                    downX = moveX;
                    downY = moveY;

                    break;
                case MotionEvent.ACTION_UP:

                    break;

                default:
                    break;
            }

            viewDragHelper.processTouchEvent(event);
            return true;

        } else {

            return false;

        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    // 设置适配器
    public void setAdapter(SlidingViewAdapter mAdapter) {

        this.mAdapter = mAdapter;

        this.count = mAdapter.getCount();

        // this.type = mAdapter.getTypeCount();

        if (convertView == null) {

            if (mAdapter != null) {

                // 第一次，convertView为空，要求用户加载、布局自己的View，返回值给显示布局
                displayView = mAdapter.getView(position, convertView);
                position++;
                // 第二次，convertView还是为空，因为上一次没有给其赋值，而是赋值给了displayView，因此还会再次加载一个View
                convertView = mAdapter.getView(position, convertView);
                position++;

            }
        }

        this.addView(convertView);
        this.addView(displayView);

        requestLayout();
    }

    class SlidingViewCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View arg0, int arg1) {
            return arg0 == convertView || arg0 == displayView;
        }

        @Override
        public void onViewDragStateChanged(int state) {

            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:// 1
                    // 开始滑动
                    isTouchable = true;

                    nextView = MySlidingView.this.getChildAt(0);
                    movingView = MySlidingView.this.getChildAt(1);

                    if (listener != null) {

                        listener.onTouchStartMove(MySlidingView.this, movingView, nextView);

                    }

                    System.out.println("onViewDragStateChanged----STATE_DRAGGING------" + state);

                    break;

                case ViewDragHelper.STATE_IDLE:// 0
                    // 停止滑动
                    isTouchable = true;

                    System.out.println("onViewDragStateChanged----STATE_IDLE------" + state);

                    if (listener != null) {

                        listener.onSmoothStop(MySlidingView.this,
                                MySlidingView.this.getChildAt(0));

                    }

                    prepareView();
                    break;
                case ViewDragHelper.STATE_SETTLING:// 2
                    // smooth
                    isTouchable = false;

                    System.out.println("onViewDragStateChanged----STATE_SETTLING------" + state);

                    break;

                default:
                    break;
            }

            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (releasedChild.getTop() > 0) {

                smoothDown(releasedChild);

            } else if (releasedChild.getTop() < 0) {

                smoothUp(releasedChild);

            }

        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            if (listener != null) {

                listener.onTouchMoving(MySlidingView.this, child, nextView);

            }

            return top;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return layoutHeight;
        }

    }

    public void prepareView() {

        if (changeView == null) {
            Log.e("prepareView", "首次进入");
            return;
        } else if (changeView == displayView) {

            Log.e("prepareView", changeView.toString());

            MySlidingView.this.removeView(displayView);

            if (position < count) {

                MySlidingView.this.addView(MySlidingView.this.mAdapter.getView(
                        position, displayView), 0);

                position++;

            } else {

                isTouchable = false;

            }

        } else if (changeView == convertView) {

            Log.e("prepareView", changeView.toString());

            MySlidingView.this.removeView(convertView);

            if (position < count) {

                MySlidingView.this.addView(MySlidingView.this.mAdapter.getView(
                        position, convertView), 0);

                position++;

            } else {

                isTouchable = false;

            }

        }

    }

    public void smoothDown(View releasedChild) {

        if (listener != null) {

            listener.onSmoothDown(MySlidingView.this, nextView);

        }

        changeView = releasedChild;
        viewDragHelper.smoothSlideViewTo(releasedChild, 0, layoutHeight);
        ViewCompat.postInvalidateOnAnimation(MySlidingView.this);
        Log.e("smoothDown", releasedChild.toString());
    }

    public void smoothUp(View releasedChild) {

        if (listener != null) {

            listener.onSmoothUp(MySlidingView.this, nextView);

        }

        changeView = releasedChild;
        viewDragHelper.smoothSlideViewTo(releasedChild, 0, -layoutHeight);
        ViewCompat.postInvalidateOnAnimation(MySlidingView.this);
        Log.e("smoothUp", releasedChild.toString());

    }

    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(MySlidingView.this);
        }
    }


    private OnSlidingStateChangeListener listener;

    public void setOnSlidingStateChangeListener(
            OnSlidingStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSlidingStateChangeListener {

        void onSmoothUp(MySlidingView swipeLayout, View nextView);

        void onTouchMoving(MySlidingView mySlidingView, View movingView,
                           View nextView);

        void onSmoothDown(MySlidingView swipeLayout, View nextView);

        void onTouchStartMove(MySlidingView swipeLayout, View movingView,
                              View nextView);

        void onSmoothStop(MySlidingView swipeLayout, View displayView);
    }

}

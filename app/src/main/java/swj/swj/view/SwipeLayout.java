package swj.swj.view;

/**
 * Created by syb on 2015/9/5.
 */
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import swj.swj.R;
import swj.swj.adpter.SwipeAdapter;

public class SwipeLayout extends FrameLayout {

    private View firstLayout;// 显示view
    private View secondLayout;// 即将切换的view
    private View convertLayout;
    private View nextView;
    private View convert;
    private View myChangeLayout;
    private View myConvertLayout;

    private SwipeAdapter mAdapter;

    private int firstHeight;
    private int secondHeight;
    private int LayoutWidth;

    public int onLayoutRight;
    public int onLayoutBottom;
    public int onLayoutTop;
    public int onLayoutLeft;

    public int count = 0;
    public int viewState = -1;

    private boolean isTouchable = true;

    // private ArrayList<View> viewList = new ArrayList<View>();

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {

        viewDragHelper = ViewDragHelper.create(this, callback);

    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View arg0, int arg1) {
            return arg0 == firstLayout || arg0 == secondLayout
                    || arg0 == convertLayout;
            // return arg0 == firstLayout || arg0 == secondLayout;
        }

        public int getViewVerticalDragRange(View child) {

            return secondHeight;
        };

        public int clampViewPositionVertical(View child, int top, int dy) {
            System.out.println("当前移动" + child);

            nextView = SwipeLayout.this.getChildAt(1);

            System.out.println(nextView);

            if (listener != null) {
                listener.onTouchMove(SwipeLayout.this, child, nextView);
            }

            return top;
        };

        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            Log.e("tag", "onViewPositionChanged");

            System.out.println("changedView.getLeft()------"
                    + changedView.getLeft());
            System.out.println("changedView.getRight()------"
                    + changedView.getRight());
            System.out.println("changedView.getTop()------"
                    + changedView.getTop());
            System.out.println("changedView.getBottom()------"
                    + changedView.getBottom());

        };

        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if (releasedChild.getTop() > 0) {

                smoothDown(releasedChild);

            } else if (releasedChild.getTop() < 0) {

                smoothUp(releasedChild);

            }

        }

        @Override
        public void onViewDragStateChanged(int state) {

            System.out
                    .println("---------------状态---------------- ：onViewDragStateChanged"
                            + state);

            if (state == 0) {
                isTouchable = true;

                if (listener != null) {
                    listener.onSmoothStop(SwipeLayout.this);
                }

                Log.e("onViewDragStateChanged", "滑动结束");
                initAllView();

            } else if (state == 1) {
                isTouchable = true;
                Log.e("onViewDragStateChanged", "开始滑动");

            } else if (state == 2) {

                isTouchable = false;

            }

            super.onViewDragStateChanged(state);
        };

    };

    public void prepareView() {

        if (myChangeLayout == firstLayout || myChangeLayout == null) {
            // SwipeLayout.this.removeView(firstLayout);
            Log.e("prepareView", "准备firstLayout");
            myConvertLayout = View.inflate(getContext(), R.layout.layout_first,
                    null);

        } else if (myChangeLayout == secondLayout) {
            // SwipeLayout.this.removeView(secondLayout);
            Log.e("prepareView", "准备secondLayout");
            myConvertLayout = View.inflate(getContext(),
                    R.layout.layout_second, null);

        } else if (myChangeLayout == convertLayout) {
            // SwipeLayout.this.removeView(convertLayout);
            Log.e("prepareView", "准备convertLayout");
            myConvertLayout = View.inflate(getContext(),
                    R.layout.layout_convert, null);

        }

    }

    public void initAllView() {

        if (myChangeLayout != null) {

            if (myChangeLayout == firstLayout) {
                // SwipeLayout.this.removeView(firstLayout);

                // myConvertLayout = View.inflate(getContext(),
                // R.layout.layout_first, null);
                SwipeLayout.this.removeView(firstLayout);
                firstLayout = null;
                firstLayout = View.inflate(getContext(), R.layout.layout_first,
                        null);
                myChangeLayout = firstLayout;
                SwipeLayout.this.addView(firstLayout, 0);
                // secondLayout.layout(0, 0, SwipeLayout.this.onLayoutRight,
                // SwipeLayout.this.onLayoutBottom);

            } else if (myChangeLayout == secondLayout) {
                // SwipeLayout.this.removeView(secondLayout);

                // myConvertLayout = View.inflate(getContext(),
                // R.layout.layout_second, null);
                SwipeLayout.this.removeView(secondLayout);
                secondLayout = null;
                secondLayout = View.inflate(getContext(),
                        R.layout.layout_second, null);
                myChangeLayout = secondLayout;
                SwipeLayout.this.addView(secondLayout, 0);
                // convertLayout.layout(0, 0, SwipeLayout.this.onLayoutRight,
                // SwipeLayout.this.onLayoutBottom);

            } else if (myChangeLayout == convertLayout) {
                // SwipeLayout.this.removeView(convertLayout);

                // myConvertLayout = View.inflate(getContext(),
                // R.layout.layout_convert, null);
                SwipeLayout.this.removeView(convertLayout);
                convertLayout = null;
                convertLayout = View.inflate(getContext(),
                        R.layout.layout_convert, null);
                myChangeLayout = convertLayout;
                SwipeLayout.this.addView(convertLayout, 0);
                // firstLayout.layout(0, 0, SwipeLayout.this.onLayoutRight,
                // SwipeLayout.this.onLayoutBottom);

            }

        }
    }

    public void smoothUp(View releasedChild) {
        System.out.println("smoothUp");

        if (listener != null) {
            listener.onSmoothUp(SwipeLayout.this, nextView);
        }

        viewDragHelper.smoothSlideViewTo(releasedChild, 0, -onLayoutBottom);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);

        myChangeLayout = releasedChild;
    }

    public void smoothDown(View releasedChild) {
        System.out.println("smoothDown");

        if (listener != null) {
            listener.onSmoothDown(SwipeLayout.this, nextView);
        }

        viewDragHelper.smoothSlideViewTo(releasedChild, 0, onLayoutBottom);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);

        myChangeLayout = releasedChild;
    }

    public void changeView(View releasedChild) {

        if (releasedChild == secondLayout) {
            System.out.println("重新布局layout1");
            convert = firstLayout;
            firstLayout = convertLayout;
            convertLayout = convert;

        } else if (releasedChild == firstLayout) {
            System.out.println("重新布局layout2");
            convert = secondLayout;
            secondLayout = convertLayout;
            convertLayout = convert;

        }

    }

    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    };

    private ViewDragHelper viewDragHelper;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        System.out.println("---------------------布局函数----------------------");

        firstLayout = View.inflate(getContext(), R.layout.layout_first, null);
        secondLayout = View.inflate(getContext(), R.layout.layout_second, null);
        convertLayout = View.inflate(getContext(), R.layout.layout_convert,
                null);

        this.addView(convertLayout);
        this.addView(secondLayout);
        this.addView(firstLayout);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        firstHeight = firstLayout.getMeasuredHeight();
        System.out.println(firstHeight);
        secondHeight = secondLayout.getMeasuredHeight();
        System.out.println(secondHeight);
        LayoutWidth = firstLayout.getMeasuredWidth();
        System.out.println(LayoutWidth);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        // super.onLayout(changed, left, top, right, bottom);

        System.out
                .println("--------------------------布局-----------------------------------");

        // 自己布局

        if (myChangeLayout == null) {

            onLayoutRight = right;
            onLayoutBottom = bottom;
            onLayoutTop = top;
            onLayoutLeft = left;

            firstLayout.layout(0, 0, right, bottom);

            secondLayout.layout(0, 0, right, bottom);

            convertLayout.layout(0, 0, right, bottom);

        } else if (myChangeLayout == firstLayout) {
            Log.e("onLayout", "firstLayout");
            firstLayout.layout(0, 0, onLayoutRight, onLayoutBottom);
            // firstLayout = myConvertLayout;
        } else if (myChangeLayout == secondLayout) {
            Log.e("onLayout", "secondLayout");
            secondLayout.layout(0, 0, onLayoutRight, onLayoutBottom);
            // firstLayout = myConvertLayout;
        } else if (myChangeLayout == convertLayout) {
            Log.e("onLayout", "convertLayout");
            convertLayout.layout(0, 0, onLayoutRight, onLayoutBottom);
            // firstLayout = myConvertLayout;
        }

        for (int i = 0; i < SwipeLayout.this.getChildCount(); i++) {

            System.out.println(SwipeLayout.this.getChildAt(i));

        }
    }

    private OnSwipeStateChangeListener listener;

    public void setOnSwipeStateChangeListener(
            OnSwipeStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeStateChangeListener {

        void onSmoothUp(SwipeLayout swipeLayout, View nextView);

        void onSmoothDown(SwipeLayout swipeLayout, View nextView);

        void onTouchMove(SwipeLayout swipeLayout, View movingView, View nextView);

        void onSmoothStop(SwipeLayout swipeLayout);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private float downX, downY;

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
                    float deltaX = moveX - downX;// x方向滑动的距离
                    float deltaY = moveY - downY;// y方向滑动的距离

                    if (Math.abs(deltaX) <= Math.abs(deltaY)) {
                        requestDisallowInterceptTouchEvent(true);// 请求父view不拦截事件
                    }

                    downX = moveX;
                    downY = moveY;
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }

            viewDragHelper.processTouchEvent(event);
            return true;
        } else {
            return false;
        }

    }

}

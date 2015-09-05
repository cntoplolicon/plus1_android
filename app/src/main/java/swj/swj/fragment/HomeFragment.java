package swj.swj.fragment;

import android.os.Bundle;
import android.view.View;

import swj.swj.R;
import swj.swj.view.SwipeLayout;


public class HomeFragment extends BaseFragment {


    private SwipeLayout sl_swipelayout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_home, null);
        return v;
    }

    @Override
    public void initData() {
        sl_swipelayout = (SwipeLayout) initView().findViewById(R.id.sl_swipelayout);

        sl_swipelayout.setOnSwipeStateChangeListener(new SwipeLayout.OnSwipeStateChangeListener() {

            @Override
            public void onTouchMove(SwipeLayout swipeLayout, View movingView,
                                    View nextView) {
                System.out.println("当前移动的是：" + movingView);
                System.out.println("下一个移动的是：" + nextView);
            }

            @Override
            public void onSmoothUp(SwipeLayout swipeLayout, View nextView) {
                System.out.println("下一个移动的是：" + nextView);
            }

            @Override
            public void onSmoothStop(SwipeLayout swipeLayout) {
                System.out.println("移动结束");
            }

            @Override
            public void onSmoothDown(SwipeLayout swipeLayout, View nextView) {
                System.out.println("下一个移动的是：" + nextView);
            }
        });
    }

}

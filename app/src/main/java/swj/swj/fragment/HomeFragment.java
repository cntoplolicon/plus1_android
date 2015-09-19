package swj.swj.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import swj.swj.R;
import swj.swj.activity.UserHomeActivity;
import swj.swj.adapter.HomeViewAdapter;
import swj.swj.bean.HomeItemBean;
import swj.swj.view.MySlidingView;

public class HomeFragment extends BaseFragment {
    private MySlidingView mSlidingView;
    private View view;
    private List<HomeItemBean> homeBeanList;
    private TextView tvUser;

    @Override
    public View initView() {
        view = View.inflate(mActivity, R.layout.fragment_home, null);

        return view;
    }

    @Override
    public void initData() {
        homeBeanList = new ArrayList<HomeItemBean>();
        for (int i = 0; i < 99; i++) {
            homeBeanList.add(new HomeItemBean(R.drawable.abc, "用户" + i, "内容" + i, "消息" + i, "浏览" + i));
        }
        mSlidingView = (MySlidingView) view.findViewById(R.id.sl_swipelayout);
        HomeViewAdapter homeViewAdapter = new HomeViewAdapter(mActivity, homeBeanList);
        mSlidingView.setAdapter(homeViewAdapter);
        tvUser = (TextView) view.findViewById(R.id.tv_user);
        init();
    }

    public void init() {
        mSlidingView.setOnSlidingStateChangeListener(new MySlidingView.OnSlidingStateChangeListener() {
            @Override
            public void onSmoothUp(MySlidingView swipeLayout, View nextView) {
                Log.d("aaaaa", "向上");
            }

            @Override
            public void onTouchMoving(MySlidingView mySlidingView, View movingView, View nextView) {
                Log.d("aaaaa", "移动");
            }

            @Override
            public void onSmoothDown(MySlidingView swipeLayout, View nextView) {
                Log.d("aaaaa", "向下");
            }

            @Override
            public void onTouchStartMove(MySlidingView swipeLayout, View movingView, View nextView) {
                Log.d("aaaaa", "开始移动");
            }

            @Override
            public void onSmoothStop(MySlidingView swipeLayout, View displayView) {
                Log.d("aaaaa", "停止移动");
//                holder.iv_image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.e("跳转", "可以了");
//                        startActivity(new Intent(mActivity, activityTemp01.class));
//                    }
//                });
                tvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("跳转", "第二个也可以了");
                        startActivity(new Intent(mActivity, UserHomeActivity.class));
                    }
                });
            }
        });

    }
}
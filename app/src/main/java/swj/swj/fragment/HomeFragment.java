package swj.swj.fragment;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import swj.swj.R;
import swj.swj.adapter.HomeViewAdapter;
import swj.swj.bean.HomeItemBean;
import swj.swj.view.HomePageLayout;

public class HomeFragment extends BaseFragment {
    private HomePageLayout mSlidingView;
    private View view;
    private List<HomeItemBean> homeBeanList;

    @Override
    public View initView() {
        view = View.inflate(mActivity, R.layout.fragment_home, null);
        return view;
    }

    @Override
    public void initData() {
        homeBeanList = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            homeBeanList.add(new HomeItemBean(R.drawable.abc, "用户" + i, "内容" + i, "消息" + i, "浏览" + i));
        }
        mSlidingView = (HomePageLayout) view.findViewById(R.id.sliding_layout);
        HomeViewAdapter homeViewAdapter = new HomeViewAdapter(mActivity, homeBeanList);
        mSlidingView.setAdapter(homeViewAdapter);
    }
}
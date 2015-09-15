package swj.swj.fragment;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import swj.swj.R;
import swj.swj.adapter.HomeViewAdapter;
import swj.swj.bean.HomeItemBean;
import swj.swj.view.MySlidingView;


public class HomeFragment extends BaseFragment {

    private MySlidingView mSlidingView;
    private View view;

    @Override
    public View initView() {
        view = View.inflate(mActivity, R.layout.fragment_home, null);
        return view;
    }

    @Override
    public void initData() {
        List<HomeItemBean> homeBeanList = new ArrayList<HomeItemBean>();
        for (int i = 0; i < 99; i++) {
            homeBeanList.add(new HomeItemBean(R.drawable.abc,"用户"+i,"内容"+i,"消息"+i,"浏览"+i));

        }
        mSlidingView = (MySlidingView) view.findViewById(R.id.sl_swipelayout);
        HomeViewAdapter HomeViewAdapter = new HomeViewAdapter(mActivity,homeBeanList);
        mSlidingView.setAdapter(HomeViewAdapter);

    }
}
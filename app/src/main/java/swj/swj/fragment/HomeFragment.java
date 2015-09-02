package swj.swj.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import swj.swj.R;


public class HomeFragment extends BaseFragment {

    //初始化布局
    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_home, null);
        return v;
    }

    //初始化数据
    @Override
    public void initData() {
        super.initData();
    }

}

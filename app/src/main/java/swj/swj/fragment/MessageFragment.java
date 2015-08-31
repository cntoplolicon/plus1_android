package swj.swj.fragment;

import android.view.View;

import swj.swj.R;


public class MessageFragment extends BaseFragment {
    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_message, null);

        return v;
    }

    @Override
    public void initData() {
        super.initData();
    }

}

package swj.swj.fragment;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import swj.swj.R;


public class FriendFragment extends BaseFragment {
    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_friend, null);

        return v;
    }

    @Override
    public void initData() {
        super.initData();
    }

}

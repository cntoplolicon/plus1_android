package swj.swj.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/*fragment基类*/

/**
 * Created by shw on 2015/8/31.
 */
public abstract class BaseFragment extends Fragment {

    public Activity mActivity;
    // Fragment被创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mActivity = getActivity();// 获取所依赖的Activity
    }
    // 初始化Fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initView();
    }

    // Fragment所依赖的Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    public abstract View initView();


    public void initData() {

    }
}

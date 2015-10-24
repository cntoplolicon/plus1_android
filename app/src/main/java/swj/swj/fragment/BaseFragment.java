package swj.swj.fragment;

import android.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/10/24.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onPause();
        MobclickAgent.onPageStart(this.getClass().toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().toString());
    }
}

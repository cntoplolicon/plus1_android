package swj.swj.fragment;

import android.view.View;

import swj.swj.R;

/**
 * Created by jiewei on 9/1/15.
 */
public class PersonalSettingsFragment extends BaseFragment {

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_personal_settings, null);
        return v;
    }
}

package swj.swj.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import swj.swj.R;
import swj.swj.activity.LoginActivity;
import swj.swj.activity.PersonalSettingsActivity;
import swj.swj.activity.RegisterStepOne;
import swj.swj.adapter.PersonalGridViewAdapter;
import swj.swj.common.LocalUserInfo;


public class MySelfFragment extends BaseFragment {

    private View headerView;
    private GridViewWithHeaderAndFooter gridView;
    private LinearLayout layoutRequestLogin;
    private TextView tvMyPublish, tvMyCollection, tvToLogin, tvToReg;

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_myself, null);
        gridView = (GridViewWithHeaderAndFooter) v.findViewById(R.id.gd_myself_content);
        headerView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView);
        PersonalGridViewAdapter adapter = new PersonalGridViewAdapter(mActivity);
        gridView.setAdapter(adapter);

        TextView tvPersonalSettings = (TextView) mActivity.findViewById(R.id.personal_settings_tv);
        tvToLogin = (TextView) v.findViewById(R.id.tv_start_login);
        tvToReg = (TextView) v.findViewById(R.id.tv_start_register);
        layoutRequestLogin = (LinearLayout) v.findViewById(R.id.ll_request_login);
        tvPersonalSettings.setOnClickListener(new MyListener());

        return v;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onResume() {
        Log.d("MyselfFragment", "onResume");
        super.onResume();
        UpdateUserInfo(LocalUserInfo.getInstance().getUserInfo("nick_name"));
    }

    private void showUserInfo(String userNickName) {
        gridView.setVisibility(View.VISIBLE);
        layoutRequestLogin.setVisibility(View.GONE);
        TextView tvNickName = (TextView) headerView.findViewById(R.id.tv_myself_nickname);
        tvNickName.setText(userNickName);
        TextView tvSex = (TextView) headerView.findViewById(R.id.tv_myself_sex);
        tvSex.setText(LocalUserInfo.getInstance().getUserInfo("sex"));
        TextView tvSign = (TextView) headerView.findViewById(R.id.tv_myself_sign);
        tvSign.setText(LocalUserInfo.getInstance().getUserInfo("sign"));
        tvMyPublish = (TextView) headerView.findViewById(R.id.tv_myself_publish);
        tvMyCollection = (TextView) headerView.findViewById(R.id.tv_myself_collect);
        tvMyPublish.setOnClickListener(new MyListener());
        tvMyCollection.setOnClickListener(new MyListener());
    }


    private void UpdateUserInfo(String userNickName) {
        if (userNickName.equals("")) {
            initLink();
        } else {
            showUserInfo(userNickName);
        }
    }

    private void initLink() {
        gridView.setVisibility(View.GONE);
        layoutRequestLogin.setVisibility(View.VISIBLE);
        tvToLogin.setOnClickListener(new MyListener());
        tvToReg.setOnClickListener(new MyListener());
    }

    class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.personal_settings_tv:
                    startActivity(new Intent(getActivity(), PersonalSettingsActivity.class));
                    break;
                case R.id.tv_start_login:
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    break;
                case R.id.tv_start_register:
                    startActivity(new Intent(mActivity, RegisterStepOne.class));
                    break;
                case R.id.tv_myself_publish:
                    tvMyPublish.setBackgroundColor(getResources().getColor(R.color.myself_content_display_selected));
                    tvMyCollection.setBackgroundColor(Color.WHITE);
                    break;
                case R.id.tv_myself_collect:
                    tvMyCollection.setBackgroundColor(getResources().getColor(R.color.myself_content_display_selected));
                    tvMyPublish.setBackgroundColor(Color.WHITE);
                    break;
            }
        }
    }
}

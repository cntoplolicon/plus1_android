package swj.swj.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import swj.swj.R;
import swj.swj.activity.PersonalSettingsActivity;
import swj.swj.adapter.PersonalGridViewAdapter;
import swj.swj.model.User;


public class MySelfFragment extends BaseFragment {

    private View headerView;
    private GridViewWithHeaderAndFooter gridView;
    private TextView tvMyPublish, tvMyCollection;

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_myself, null);
        gridView = (GridViewWithHeaderAndFooter) v.findViewById(R.id.gd_myself_content);
        headerView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView);
        PersonalGridViewAdapter adapter = new PersonalGridViewAdapter(mActivity);
        gridView.setAdapter(adapter);

        TextView tvPersonalSettings = (TextView) mActivity.findViewById(R.id.personal_settings_tv);
        tvPersonalSettings.setOnClickListener(onClick);

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
        UpdateUserInfo(User.current.getNickname());
    }

    private void showUserInfo(String userNickName) {
        TextView tvNickName = (TextView) headerView.findViewById(R.id.tv_myself_nickname);
        tvNickName.setText(userNickName);
//        TextView tvSex = (TextView) headerView.findViewById(R.id.tv_myself_sex);
//        tvSex.setText(LocalUserInfo.getInstance().getUserInfo("sex"));
        TextView tvSign = (TextView) headerView.findViewById(R.id.tv_myself_sign);
        tvSign.setText("风雨之后一定是彩虹");
        tvMyPublish = (TextView) headerView.findViewById(R.id.tv_myself_publish);
        tvMyCollection = (TextView) headerView.findViewById(R.id.tv_myself_collect);
        tvMyPublish.setOnClickListener(onClick);
        tvMyCollection.setOnClickListener(onClick);
    }


    private void UpdateUserInfo(String userNickName) {
        showUserInfo(userNickName);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.personal_settings_tv:
                    startActivity(new Intent(getActivity(), PersonalSettingsActivity.class));
                    break;
                case R.id.tv_myself_publish:
                    tvMyPublish.setBackgroundResource(R.drawable.personal_tv_border_left_pressed);
                    tvMyCollection.setBackgroundResource(R.drawable.personal_tv_border_right_unpressed);
                    break;
                case R.id.tv_myself_collect:
                    tvMyPublish.setBackgroundResource(R.drawable.personal_tv_border_left_unpressed);
                    tvMyCollection.setBackgroundResource(R.drawable.personal_tv_border_right_pressed);
                    break;
            }
        }
    };
}

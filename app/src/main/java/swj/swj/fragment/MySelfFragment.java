package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import swj.swj.R;
import swj.swj.activity.PersonalSettingsActivity;
import swj.swj.adapter.PersonalGridViewAdapter;
import swj.swj.model.User;


public class MySelfFragment extends Fragment {

    private View headerView;
    private GridViewWithHeaderAndFooter gridView;
    private TextView tvMyPublish, tvMyCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        gridView = (GridViewWithHeaderAndFooter) view.findViewById(R.id.gd_myself_content);
        headerView = inflater.inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView);
        PersonalGridViewAdapter adapter = new PersonalGridViewAdapter(getActivity());
        gridView.setAdapter(adapter);

        ImageView ivPersonalSettings = (ImageView) getActivity().findViewById(R.id.iv_settings);
        ivPersonalSettings.setOnClickListener(onClick);

        return view;
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
        TextView tvSign = (TextView) headerView.findViewById(R.id.tv_myself_sign);
        tvSign.setText(User.current.getBiography());
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
                case R.id.iv_settings:
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

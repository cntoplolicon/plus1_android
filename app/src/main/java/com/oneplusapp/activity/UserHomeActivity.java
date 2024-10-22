package com.oneplusapp.activity;

import android.os.Bundle;

import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.User;
import com.oneplusapp.view.ActionBarLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserHomeActivity extends BaseActivity {

    @Bind(R.id.action_bar)
    ActionBarLayout actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        ButterKnife.bind(this);
        String userJson = getIntent().getStringExtra("user_json");
        User user = CommonMethods.createDefaultGson().fromJson(userJson, User.class);
        actionBar.setPageTitle(user.getNickname());
    }

}

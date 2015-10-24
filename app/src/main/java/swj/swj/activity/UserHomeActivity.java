package swj.swj.activity;

import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.model.User;
import swj.swj.view.ActionBarLayout;

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

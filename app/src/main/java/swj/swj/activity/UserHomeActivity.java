package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.adapter.UserPostsGridViewAdapter;
import swj.swj.common.CommonMethods;
import swj.swj.model.User;
import swj.swj.view.HeaderGridView;

public class UserHomeActivity extends Activity {

    @Bind(R.id.tv_biography)
    TextView tvBiography;

    @Bind(R.id.tv_nickname)
    TextView tvNickname;

    @Bind(R.id.iv_gender)
    ImageView ivGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        String userJson = getIntent().getStringExtra("user_json");
        User user = CommonMethods.createDefaultGson().fromJson(userJson, User.class);
        HeaderGridView gridView = (HeaderGridView) findViewById(R.id.grid_view_user_authored_posts);
        View headerView = LayoutInflater.from(this).inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView);

        ButterKnife.bind(this, headerView);
        updateUserInfo(user);

        gridView.setAdapter(new UserPostsGridViewAdapter(this, user.getId()));
    }

    private void updateUserInfo(User user) {
        tvBiography.setText(user.getBiography());
        tvNickname.setText(user.getNickname());
        if (user.getGender() == User.GENDER_UNKNOWN) {
            ivGender.setVisibility(View.INVISIBLE);
        } else {
            ivGender.setVisibility(View.VISIBLE);
            int resource = user.getGender() == User.GENDER_FEMALE ?
                    R.drawable.icon_woman : R.drawable.icon_man;
            ivGender.setImageResource(resource);
        }
    }
}

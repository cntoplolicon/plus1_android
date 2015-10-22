package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.adapter.UserPostsGridViewAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.common.CommonMethods;
import swj.swj.model.User;
import swj.swj.view.ActionBarLayout;
import swj.swj.view.HeaderGridView;

public class UserHomeActivity extends Activity {

    private HeaderViewHolder headerViewHolder = new HeaderViewHolder();

    @Bind(R.id.action_bar)
    ActionBarLayout actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        String userJson = getIntent().getStringExtra("user_json");
        User user = CommonMethods.createDefaultGson().fromJson(userJson, User.class);
        HeaderGridView gridView = (HeaderGridView) findViewById(R.id.grid_view_user_authored_posts);
        View headerView = LayoutInflater.from(this).inflate(R.layout.user_home_gridview_header, null);
        gridView.addHeaderView(headerView, null, false);

        ButterKnife.bind(headerViewHolder, headerView);
        ButterKnife.bind(this);
        updateUserInfo(user);

        gridView.setAdapter(new UserPostsGridViewAdapter(this, user.getId()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserHomeActivity.this, CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });
    }

    private void updateUserInfo(User user) {
        headerViewHolder.tvBiography.setText(user.getBiography());
        headerViewHolder.tvNickname.setText(user.getNickname());
        actionBar.setPageTitle(user.getNickname());
        switch (user.getGender()) {
            case User.GENDER_MALE:
                actionBar.setPageTitleColor(R.color.personal_common_male_username);
                break;
            case User.GENDER_FEMALE:
                actionBar.setPageTitleColor(R.color.personal_common_female_username);
                break;
            default:
                actionBar.setPageTitleColor(R.color.unknown_gender);
                break;
        }
        String imageUrl = user.getAvatar();
        if (imageUrl == null) {
            headerViewHolder.ivAvatar.setImageResource(R.drawable.default_useravatar);
        } else {
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + imageUrl, headerViewHolder.ivAvatar);
        }
        if (user.getGender() == User.GENDER_UNKNOWN) {
            headerViewHolder.ivGender.setVisibility(View.INVISIBLE);
        } else {
            headerViewHolder.ivGender.setVisibility(View.VISIBLE);
            int resource = user.getGender() == User.GENDER_FEMALE ?
                    R.drawable.icon_woman : R.drawable.icon_man;
            headerViewHolder.ivGender.setImageResource(resource);
        }
        CommonMethods.chooseNicknameColorViaGender(headerViewHolder.tvNickname, user, getBaseContext());
    }

    static class HeaderViewHolder {
        @Bind(R.id.tv_biography)
        TextView tvBiography;

        @Bind(R.id.tv_nickname)
        TextView tvNickname;

        @Bind(R.id.iv_gender)
        ImageView ivGender;

        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
    }
}

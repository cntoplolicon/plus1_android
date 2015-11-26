package com.oneplusapp.activity;

import android.os.Bundle;
import android.os.Handler;

import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.User;
import com.oneplusapp.view.AdjustableImageView;
import com.oneplusapp.view.UserAvatarImageView;
import com.oneplusapp.view.UserNicknameTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeleteCardActivity extends BaseActivity {

    @Bind(R.id.iv_avatar)
    UserAvatarImageView ivAvatar;
    @Bind(R.id.tv_nickname)
    UserNicknameTextView tvNickname;
    @Bind(R.id.iv_image)
    AdjustableImageView ivImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_card);

        ButterKnife.bind(this);

        String userJson = getIntent().getStringExtra("user_json");
        User user = CommonMethods.createDefaultGson().fromJson(userJson, User.class);
        ivAvatar.setUser(user);
        tvNickname.setUser(user);
        ivImage.setImageResource(R.drawable.delete_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }
}

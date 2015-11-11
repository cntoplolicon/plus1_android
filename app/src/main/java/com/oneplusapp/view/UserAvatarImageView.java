package com.oneplusapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.activity.ShowImageActivity;
import com.oneplusapp.activity.UserHomeActivity;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by silentgod on 15-11-11.
 */
public class UserAvatarImageView extends CircleImageView {

    private boolean linkToUserHome;

    public UserAvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserAvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UserAvatarImageView, 0, 0);

        try {
            linkToUserHome = a.getBoolean(R.styleable.UserAvatarImageView_link_to_user_home, true);
        } finally {
            a.recycle();
        }

    }

    public void setUser(final User user) {
        ImageLoader.getInstance().cancelDisplayTask(this);
        if (user.getAvatar() != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), this);
        } else {
            setImageResource(R.drawable.default_user_avatar);
        }
        if (linkToUserHome) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserHomeActivity.class);
                    intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(user));
                    getContext().startActivity(intent);
                }
            });
        } else {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShowImageActivity.class);
                    intent.putExtra("image_url", user.getAvatar());
                    getContext().startActivity(intent);
                }
            });
        }
    }

    public boolean isLinkToUserHome() {
        return linkToUserHome;
    }

}

package com.oneplusapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.oneplusapp.R;
import com.oneplusapp.activity.ShowImageActivity;
import com.oneplusapp.activity.UserHomeActivity;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by silentgod on 15-11-11.
 */
public class CustomUserAvatarView extends CircleImageView {

    private final static int BORDER_WIDTH = 1;

    private boolean linkToUserHome;

    public CustomUserAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomUserAvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomUserAvatarView, 0, 0);

        try {
            linkToUserHome = a.getBoolean(R.styleable.CustomUserAvatarView_link_to_user_home, true);
        } finally {
            a.recycle();
        }

        setBorderColor(ContextCompat.getColor(getContext(), R.color.common_yellow));
        setBorderWidth(BORDER_WIDTH * 2);
    }

    public void setUser(final User user) {
        if (linkToUserHome) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserHomeActivity.class);
                    intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(user));
                    getContext().startActivity(intent);
                }
            });
        } else if (user.getId() != User.current.getId()) {
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

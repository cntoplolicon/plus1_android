package com.oneplusapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.oneplusapp.R;
import com.oneplusapp.model.User;

/**
 * Created by silentgod on 15-11-10.
 */
public class UserNicknameTextView extends TextView {

    private boolean hideGenderIcon;
    private String userNickname;
    private int genderIcon;
    private Context context;

    public UserNicknameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    public UserNicknameTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomNicknameLayout, 0, 0);

        try {
            hideGenderIcon = a.getBoolean(R.styleable.CustomNicknameLayout_hide_gender_icon, false);
        } finally {
            a.recycle();
        }
    }

    public void setUser(User user) {
        userNickname = user.getNickname();
        setText(userNickname);
        int textColor;
        switch (user.getGender()) {
            case User.GENDER_FEMALE:
                genderIcon = R.drawable.icon_woman;
                textColor = R.color.personal_common_female_username;
                break;
            case User.GENDER_MALE:
                genderIcon = R.drawable.icon_man;
                textColor = R.color.personal_common_male_username;
                break;
            default:
                genderIcon = 0;
                textColor = R.color.unknown_gender;
                break;
        }
        setTextColor(ContextCompat.getColor(context, textColor));
        if (!hideGenderIcon) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, genderIcon, 0);
        }
    }

    public void setHideGenderIcon(boolean hideGenderIcon) {
        this.hideGenderIcon = hideGenderIcon;
        int iconResource = hideGenderIcon ? 0 : genderIcon;
        setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResource, 0);
    }

    public boolean isHideGenderIcon() {
        return hideGenderIcon;
    }
}
